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
    public OrderResponseDto createOrder(OrderCreateRequestDto request) {
        logger.info("주문 생성 요청 - memberId: {}", request.getMemberId());

        // 1. 회원 확인
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 배송지 확인
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));

        // 3. 쿠폰 확인 (쿠폰 없이 주문 가능)
        MemberCoupon memberCoupon = null;
        if (request.getMemberCouponId() != null) {
            memberCoupon = memberCouponRepository.findById(request.getMemberCouponId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_COUPON_NOT_FOUND));
        }

        // 4. 주문번호 생성
        String orderNumber = generateUniqueOrderNumber();

        // 5. 주문 엔티티 생성 및 저장
        Order order = orderRepository.save(request.toEntity(member, memberCoupon, deliveryAddress, orderNumber));

        // 6. 주문 상품 처리 (재고 검증 → 차감 → OrderItem 생성)
        for (OrderItemRequestDto itemDto : request.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

         // 재고 검증 + 차감을 서비스에서 직접
            if (product.getOptionList().isEmpty()) {
                // stock 필드가 없으니 주문 수량만 검증
                throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK);
            }

            // OrderItem 생성 및 저장
            OrderItem orderItem = itemDto.toEntity(product, order);
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

    // 중복 없는 주문번호 생성
    private String generateUniqueOrderNumber() {
        String orderNumber;
        do {
            orderNumber = OrderNumberGenerator.generate();
        } while (orderRepository.existsByOrderNumber(orderNumber));
        return orderNumber;
    }
}