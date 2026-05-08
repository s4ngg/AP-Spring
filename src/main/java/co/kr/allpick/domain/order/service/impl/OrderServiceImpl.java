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
import co.kr.allpick.domain.order.dto.SellerOrderResponseDto;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.repository.SellerRepository;
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
    private final SellerRepository sellerRepository;

    @Override
    @Transactional
    public OrderResponseDto createOrder(Long memberId ,OrderCreateRequestDto request) {
        logger.info("주문 ?�성 ?�청 - memberId: {}", memberId);


        // 1. ?�용??�?배송지 ?�인
        Member member = memberRepository.findById(memberId)

                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 배송지 ?�인
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));

        // 3. 쿠폰 ?�인 �??�인금액 계산
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
        }

// 4. 주문번호 ?�성
        String orderNumber = generateUniqueOrderNumber();

// 5. 주문 ?�티???�성 �??�??
        Order order = orderRepository.save(request.toEntity(member, memberCoupon, deliveryAddress, orderNumber, discountAmount));
        for (OrderItemRequestDto itemDto : request.getOrderItems()) {
            // 6-1. ?�품 ?�보 조회
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            // 6-2. [중요] ?�용?��? ?�택???�정 ?�션 찾기 (for�??�용)
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

            // 6-3. [진짜 ?�고 차감] ?�티???��? 메서???�출
            // (ProductOption ?�티?�에 removeStock 메서?��? 미리 구현?�어 ?�어????
            targetOption.removeStock(itemDto.getQuantity());

            // 6-4. OrderItem ?�성 �??�??(방금 ?�정???�티??구조 반영)
            // toEntity 메서?��? ?�정?�셨?�면 그�?�??�출, ?�니�?빌더 직접 ?�용
            OrderItem orderItem = itemDto.toEntity(product, targetOption, order);
            
            orderItemRepository.save(orderItem);
            order.addOrderItem(orderItem);
        }

        // 7. �?금액 DB?�서 집계
        BigDecimal totalAmount = orderItemRepository.sumTotalPriceByOrderId(order.getOrderId());
        order.updateTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        logger.info("주문 ?�성 ?�료 - orderNumber: {}", orderNumber);
        return OrderResponseDto.from(savedOrder, savedOrder.getOrderItems());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Long orderId) {
        logger.info("주문 조회 - orderId: {}", orderId);
        // fetch join?�로 OrderItem ??번에 조회
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
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
        logger.info("배송지 추�? - memberId: {}", memberId);

        // 중복 배송지 검�?
        if (deliveryAddressRepository.existsByMemberIdAndAddressAndAddressDetail(
                memberId, request.getAddress(), request.getAddressDetail())) {
            throw new BusinessException(ErrorCode.DELIVERY_ADDRESS_DUPLICATE);
        }

        DeliveryAddressResponseDto saved = DeliveryAddressResponseDto.from(
                deliveryAddressRepository.save(request.toEntity(memberId)));

        logger.info("배송지 추�? ?�료 - addressId: {}", saved.getAddressId());
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
        logger.info("[OrderService] 배송지 ?�정 - memberId: {}, addressId: {}", memberId, addressId);
        DeliveryAddress address = findAddressAndValidateOwner(memberId, addressId);
        validateAddressNotUsedInOrder(addressId);
        address.update(request.getRecipientName(), request.getPhone(), request.getZipCode(),
                request.getAddress(), request.getAddressDetail(), request.isDefault());
        return DeliveryAddressResponseDto.from(address);
    }

    @Override
    @Transactional
    public void deleteDeliveryAddress(Long memberId, Long addressId) {
        logger.info("[OrderService] 배송지 ??�� - memberId: {}, addressId: {}", memberId, addressId);
        DeliveryAddress address = findAddressAndValidateOwner(memberId, addressId);
        validateAddressNotUsedInOrder(addressId);
        address.delete();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SellerOrderResponseDto> getSellerOrders(Long memberId) {
        Seller seller = sellerRepository.findByMember_IdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLER));
        return orderRepository.findBySellerIdWithDetails(seller.getSellerId())
                .stream()
                .map(order -> SellerOrderResponseDto.from(order, order.getOrderItems()))
                .toList();
    }

    // 배송지 조회 + ?�유�?검�?
    private DeliveryAddress findAddressAndValidateOwner(Long memberId, Long addressId) {
        DeliveryAddress address = deliveryAddressRepository.findById(addressId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));
        if (!address.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ADDRESS);
        }
        return address;
    }

    // 주문???�용??배송지 ?�정/??�� 방�?
    private void validateAddressNotUsedInOrder(Long addressId) {
        if (orderRepository.existsByDeliveryAddress_AddressId(addressId)) {
            throw new BusinessException(ErrorCode.ADDRESS_CANNOT_MODIFY);
        }
    }

    // 중복 ?�는 주문번호 ?�성
    private String generateUniqueOrderNumber() {
        String orderNumber;
        do {
            orderNumber = OrderNumberGenerator.generate();
        } while (orderRepository.existsByOrderNumber(orderNumber));
        return orderNumber;
    }
}