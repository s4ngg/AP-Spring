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

    
    
    // OrderCreateRequestDto 안에 memberId, addressId, memberCouponId 모두 있음. 
    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderCreateRequestDto request) {
        logger.info("주문 생성 요청 - memberId: {}", request.getMemberId());

        // 1. 사용자 및 배송지 확인
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));

        // 2. 쿠폰 확인 (Optional 활용으로 간결하게 처리)
        MemberCoupon memberCoupon = null;
        if (request.getMemberCouponId() != null) {
            memberCoupon = memberCouponRepository.findById(request.getMemberCouponId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_COUPON_NOT_FOUND));
        }

        // 3. 주문 엔티티 생성 및 초기 저장 (ID와 연관관계를 위해 먼저 생성)
        String orderNumber = generateUniqueOrderNumber();
        Order order = orderRepository.save(request.toEntity(member, memberCoupon, deliveryAddress, orderNumber));

        // 4. 주문 항목 생성 및 상품 처리
        for (OrderItemRequestDto itemDto : request.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            // TODO: 재고 차감 로직 (예: product.decreaseStock(itemDto.getQuantity()))
            
            OrderItem orderItem = itemDto.toEntity(product, order);
            order.addOrderItem(orderItem); // Order 내부 리스트에 추가
        }

        // 5. 총 금액 업데이트 (기존 로직 유지)
        BigDecimal totalAmount = order.getOrderItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
       
        order.updateTotalAmount(totalAmount);

        // 6. 결과 반환 
        // @Transactional이 걸려있으므로, 별도의 save 호출 없이도 변경 사항(totalAmount)이 DB에 반영됩니다.
        logger.info("주문 생성 완료 - orderNumber: {}", orderNumber);
        return OrderResponseDto.from(order, order.getOrderItems());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Long orderId) {
        logger.info("주문 조회 - orderId: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        List<OrderItem> orderItems = orderItemRepository.findByOrder_OrderId(orderId);
        return OrderResponseDto.from(order, orderItems);
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

    private DeliveryAddress findAddressAndValidateOwner(Long memberId, Long addressId) {
        DeliveryAddress address = deliveryAddressRepository.findById(addressId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));
        if (!address.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ADDRESS);
        }
        return address;
    }

    private void validateAddressNotUsedInOrder(Long addressId) {
        if (orderRepository.existsByDeliveryAddress_AddressId(addressId)) {
            throw new BusinessException(ErrorCode.ADDRESS_CANNOT_MODIFY);
        }
    }

//    주문 번호 생성
    private String generateUniqueOrderNumber() {
        String orderNumber;
        do {
            orderNumber = OrderNumberGenerator.generate();
        } while (orderRepository.existsByOrderNumber(orderNumber));
        return orderNumber;
    }
}
