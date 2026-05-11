package co.kr.allpick.domain.order.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.coupon.entity.MemberCoupon;
import co.kr.allpick.domain.coupon.repository.MemberCouponRepository;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.order.dto.DeliveryAddressRequestDto;
import co.kr.allpick.domain.order.dto.DeliveryAddressResponseDto;
import co.kr.allpick.domain.order.dto.OrderCreateRequestDto;
import co.kr.allpick.domain.order.dto.OrderItemRequestDto;
import co.kr.allpick.domain.order.dto.OrderResponseDto;
import co.kr.allpick.domain.order.dto.PaymentResponseDto;
import co.kr.allpick.domain.order.entity.DeliveryAddress;
import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.entity.OrderItem;
import co.kr.allpick.domain.order.entity.Payment;
import co.kr.allpick.domain.order.repository.DeliveryAddressRepository;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.domain.order.repository.OrderRepository;
import co.kr.allpick.domain.order.repository.PaymentRepository;
import co.kr.allpick.domain.order.service.OrderService;
import co.kr.allpick.domain.order.util.OrderNumberGenerator;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.entity.ProductOption;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final MemberRepository memberRepository;
    private final MemberCouponRepository memberCouponRepository;

    @Override
    @Transactional
    public OrderResponseDto createOrder(Long memberId ,OrderCreateRequestDto request) {
        logger.info("주문 생성 요청 - memberId: {}", memberId);


        // 1. 사용자 및 배송지 확인
        Member member = memberRepository.findById(memberId)

                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 배송지 확인
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));

        // 3. 쿠폰 확인 및 할인금액 계산
        MemberCoupon memberCoupon = null;
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (request.getMemberCouponId() != null) {
            memberCoupon = memberCouponRepository.findById(request.getMemberCouponId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_COUPON_NOT_FOUND));

            BigDecimal totalProductPrice = request.getOrderItems().stream()
                    .map(item -> productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND))
                            .getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            var coupon = memberCoupon.getCoupon();
            if (coupon.getDiscountType().name().equals("PERCENT")) {
                discountAmount = totalProductPrice
                        .multiply(coupon.getDiscountValue())
                        .divide(BigDecimal.valueOf(100));
                if (coupon.getMaxDiscount() != null) {
                    discountAmount = discountAmount.min(coupon.getMaxDiscount());
                }
            } else {
                discountAmount = coupon.getDiscountValue();
            }
            BigDecimal maxDiscount = totalProductPrice.add(BigDecimal.valueOf(request.getShippingFee()));
            discountAmount = discountAmount.min(maxDiscount);
        }

// 4. 주문번호 생성
        String orderNumber = generateUniqueOrderNumber();

// 5. 주문 엔티티 생성 및 저장
        Order order = orderRepository.save(request.toEntity(member, memberCoupon, deliveryAddress, orderNumber, discountAmount));
        for (OrderItemRequestDto itemDto : request.getOrderItems()) {
            // 6-1. 상품 정보 조회
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            // 6-2. [중요] 사용자가 선택한 특정 옵션 찾기 (for문 활용)
            ProductOption targetOption = null;
            for (ProductOption option : product.getOptionList()) {
                if (option.getOptionId().equals(itemDto.getOptionId())) {
                    targetOption = option;
                    break;
                }
                
            }

            if (targetOption == null) {
                throw new BusinessException(ErrorCode.PRODUCT_OPTION_NOT_FOUND);
            }

            // 6-3. [진짜 재고 차감] 엔티티 내부 메서드 호출
            // (ProductOption 엔티티에 removeStock 메서드가 미리 구현되어 있어야 함)
            targetOption.removeStock(itemDto.getQuantity());

            // 6-4. OrderItem 생성 및 저장 (방금 수정한 엔티티 구조 반영)
            // toEntity 메서드를 수정하셨다면 그대로 호출, 아니면 빌더 직접 사용
            OrderItem orderItem = itemDto.toEntity(product, targetOption, order);
            
            orderItemRepository.save(orderItem);
            order.addOrderItem(orderItem);
        }

        // 7. 총 금액 DB에서 집계
        BigDecimal totalAmount = orderItemRepository.sumTotalPriceByOrderId(order.getOrderId());
        order.updateTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        logger.info("주문 생성 완료 - orderNumber: {}", orderNumber);
        return OrderResponseDto.from(savedOrder, savedOrder.getOrderItems());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Long orderId) {
        logger.info("주문 조회 - orderId: {}", orderId);
        // fetch join으로 OrderItem 한 번에 조회
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        return OrderResponseDto.from(order, order.getOrderItems());
    }

    @Override
    @Transactional
    public OrderResponseDto cancelOrder(Long memberId, Long orderId) {
        logger.info("주문 취소 요청 - memberId: {}, orderId: {}", memberId, orderId);
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        validateOrderOwner(memberId, order);
        validateOrderCancelable(order);
        restoreOrderItemStock(order);
        order.updateStatus(Order.OrderStatus.CANCELLED);

        logger.info("주문 취소 완료 - orderId: {}", orderId);
        return OrderResponseDto.from(order, order.getOrderItems());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPayment(Long orderId) {
        logger.info("결제 조회 - orderId: {}", orderId);
        Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
        return PaymentResponseDto.from(payment);
    }

    @Override
    @Transactional
    public DeliveryAddressResponseDto addDeliveryAddress(Long memberId, DeliveryAddressRequestDto request) {
        logger.info("배송지 추가 - memberId: {}", memberId);

        // 중복 배송지 검증
        if (deliveryAddressRepository.existsByMemberIdAndAddressAndAddressDetail(
                memberId, request.getAddress(), request.getAddressDetail())) {
            throw new BusinessException(ErrorCode.DELIVERY_ADDRESS_DUPLICATE);
        }

        DeliveryAddressResponseDto saved = DeliveryAddressResponseDto.from(
                deliveryAddressRepository.save(request.toEntity(memberId)));

        logger.info("배송지 추가 완료 - addressId: {}", saved.getAddressId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryAddressResponseDto> getDeliveryAddresses(Long memberId) {
        logger.info("배송지 목록 조회 - memberId: {}", memberId);
        return deliveryAddressRepository.findByMemberIdAndDeletedAtIsNull(memberId)
                .stream()
                .map(DeliveryAddressResponseDto::from)
                .toList();
    }

    @Override
    @Transactional
    public DeliveryAddressResponseDto updateDeliveryAddress(Long memberId, Long addressId, DeliveryAddressRequestDto request) {
        logger.info("[OrderService] 배송지 수정 - memberId: {}, addressId: {}", memberId, addressId);
        DeliveryAddress address = findAddressAndValidateOwner(memberId, addressId);
        validateAddressNotUsedInOrder(addressId);
        address.update(request.getRecipientName(), request.getPhone(), request.getZipCode(),
                request.getAddress(), request.getAddressDetail(), request.isDefault());
        return DeliveryAddressResponseDto.from(address);
    }

    @Override
    @Transactional
    public void deleteDeliveryAddress(Long memberId, Long addressId) {
        logger.info("[OrderService] 배송지 삭제 - memberId: {}, addressId: {}", memberId, addressId);
        DeliveryAddress address = findAddressAndValidateOwner(memberId, addressId);
        validateAddressNotUsedInOrder(addressId);
        address.delete();
    }

    // 배송지 조회 + 소유권 검증
    private DeliveryAddress findAddressAndValidateOwner(Long memberId, Long addressId) {
        DeliveryAddress address = deliveryAddressRepository.findById(addressId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));
        if (!address.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ADDRESS);
        }
        return address;
    }

    // 주문에 사용된 배송지 수정/삭제 방지
    private void validateAddressNotUsedInOrder(Long addressId) {
        if (orderRepository.existsByDeliveryAddress_AddressId(addressId)) {
            throw new BusinessException(ErrorCode.ADDRESS_CANNOT_MODIFY);
        }
    }

    private void validateOrderOwner(Long memberId, Order order) {
        if (!order.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ORDER);
        }
    }

    private void validateOrderCancelable(Order order) {
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
        }
    }

    private void restoreOrderItemStock(Order order) {
        order.getOrderItems().forEach(orderItem ->
                orderItem.getProductOption().restoreStock(orderItem.getQuantity())
        );
    }

    // 중복 없는 주문번호 생성
    private String generateUniqueOrderNumber() {
        String orderNumber;
        do {
            orderNumber = OrderNumberGenerator.generate();
        } while (orderRepository.existsByOrderNumber(orderNumber));
        return orderNumber;
    }
}
