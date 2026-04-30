package co.kr.allpick.domain.order.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
    
    // OrderCreateRequestDto 안에 memberId, addressId, memberCouponId 모두 있음. 
    public OrderResponseDto createOrder(OrderCreateRequestDto request) {
        logger.info("주문 생성 요청 - memberId: {}", request.getMemberId());
        
        
        // 사용자 확인 
        Member member = memberRepository.findById(request.getMemberId())
        		.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        
        // 배송지 확인
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));

        // 쿠폰 확인
        MemberCoupon memberCoupon = memberCouponRepository.findById(request.getMemberCouponId())
        		.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_COUPON_NOT_FOUND));
        		
        // 주문번호 부여 
        String orderNumber = generateUniqueOrderNumber();
        
        // 모든 검증 통과 -> 주문 엔티티 생성
        Order savedOrder = orderRepository.save(request.toEntity(member, memberCoupon, deliveryAddress, orderNumber));

        // 3. 주문 상품 생성
        List<OrderItem> orderItems = request.getOrderItems().stream()
                .map(itemDto -> {
                	// orderItem 엔티티의 행과 매칭될 상품 조회 후 orderItem 객체 생성 
                	Product product = productRepository.findById(itemDto.getProductId())
                			.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
                	
                	OrderItem orderItem = itemDto.toEntity(product, savedOrder);
                	return orderItemRepository.save(orderItem);
                })
                .collect(Collectors.toList());

        // 4. 총 금액 업데이트
        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        savedOrder.updateTotalAmount(totalAmount);
        orderRepository.save(savedOrder);

        logger.info("주문 생성 완료 - orderNumber: {}", orderNumber);
        return OrderResponseDto.from(savedOrder, orderItems);
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

        // #26 회원 존재 검증
        memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // #26 중복 배송지 검증
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
        return deliveryAddressRepository.findByMemberId(memberId)
                .stream()
                .map(DeliveryAddressResponseDto::from)
                .collect(Collectors.toList());
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