package co.kr.allpick.domain.order.service;

import co.kr.allpick.domain.order.dto.DeliveryAddressRequestDto;
import co.kr.allpick.domain.order.dto.DeliveryAddressResponseDto;
import co.kr.allpick.domain.order.dto.OrderCreateRequestDto;
import co.kr.allpick.domain.order.dto.OrderItemResponseDto;
import co.kr.allpick.domain.order.dto.OrderResponseDto;
import co.kr.allpick.domain.order.dto.PaymentResponseDto;
import co.kr.allpick.domain.order.entity.DeliveryAddress;
import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.entity.OrderItem;
import co.kr.allpick.domain.order.entity.Payment;
import co.kr.allpick.domain.order.repository.impl.DeliveryAddressRepositoryImpl;
import co.kr.allpick.domain.order.repository.impl.OrderItemRepositoryImpl;
import co.kr.allpick.domain.order.repository.impl.OrderRepositoryImpl;
import co.kr.allpick.domain.order.repository.impl.PaymentRepositoryImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger logger = LogManager.getLogger(OrderService.class);

    private final OrderRepositoryImpl orderRepository;
    private final OrderItemRepositoryImpl orderItemRepository;
    private final PaymentRepositoryImpl paymentRepository;
    private final DeliveryAddressRepositoryImpl deliveryAddressRepository;

    public OrderService(
            @Qualifier("orderRepositoryHandler") OrderRepositoryImpl orderRepository,
            @Qualifier("orderItemRepositoryHandler") OrderItemRepositoryImpl orderItemRepository,
            @Qualifier("paymentRepositoryHandler") PaymentRepositoryImpl paymentRepository,
            @Qualifier("deliveryAddressRepositoryHandler") DeliveryAddressRepositoryImpl deliveryAddressRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.deliveryAddressRepository = deliveryAddressRepository;
    }

    @Transactional
    public OrderResponseDto createOrder(Long memberId, OrderCreateRequestDto request) {
        logger.info("주문 생성 요청 - memberId: {}", memberId);

        deliveryAddressRepository.findById(request.getAddressId());

        String orderNumber = generateOrderNumber();

        Order order = Order.builder()
                .memberId(memberId)
                .addressId(request.getAddressId())
                .orderNumber(orderNumber)
                .totalAmount(BigDecimal.ZERO)
                .shippingFee(3000)
                .status(Order.OrderStatus.PENDING)
                .orderedAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = buildOrderItems(savedOrder, request.getOrderItems());
        orderItems.forEach(orderItemRepository::save);

        BigDecimal totalAmount = calculateTotalAmount(orderItems);
        savedOrder.updateTotalAmount(totalAmount);
        orderRepository.save(savedOrder);

        logger.info("주문 생성 완료 - orderNumber: {}", orderNumber);
        return toOrderResponseDto(savedOrder, orderItems);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Long orderId) {
        logger.info("주문 조회 - orderId: {}", orderId);
        Order order = orderRepository.findById(orderId);
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return toOrderResponseDto(order, orderItems);
    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getPayment(Long orderId) {
        logger.info("결제 조회 - orderId: {}", orderId);
        Payment payment = paymentRepository.findByOrderId(orderId);
        return toPaymentResponseDto(payment);
    }

    @Transactional
    public DeliveryAddressResponseDto addDeliveryAddress(Long memberId, DeliveryAddressRequestDto request) {
        logger.info("배송지 추가 - memberId: {}", memberId);
        DeliveryAddress deliveryAddress = DeliveryAddress.builder()
                .memberId(memberId)
                .recipientName(request.getRecipientName())
                .phone(request.getPhone())
                .zipCode(request.getZipCode())
                .address(request.getAddress())
                .addressDetail(request.getAddressDetail())
                .isDefault(request.isDefault())
                .build();
        DeliveryAddress saved = deliveryAddressRepository.save(deliveryAddress);
        logger.info("배송지 추가 완료 - addressId: {}", saved.getAddressId());
        return toDeliveryAddressResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<DeliveryAddressResponseDto> getDeliveryAddresses(Long memberId) {
        logger.info("배송지 목록 조회 - memberId: {}", memberId);
        return deliveryAddressRepository.findByMemberId(memberId)
                .stream()
                .map(this::toDeliveryAddressResponseDto)
                .collect(Collectors.toList());
    }

    // ==================== 헬퍼 메서드 ====================

    private String generateOrderNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%06d", (int)(Math.random() * 1000000));
        return "ORD-" + date + "-" + random;
    }

    private List<OrderItem> buildOrderItems(Order order, List<OrderCreateRequestDto.OrderItemRequestDto> items) {
        return items.stream()
                .map(item -> OrderItem.builder()
                        .order(order)
                        .productId(item.getProductId())
                        .productName("상품명")
                        .productPrice(BigDecimal.valueOf(10000))
                        .quantity(item.getQuantity())
                        .totalPrice(BigDecimal.valueOf(10000).multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderResponseDto toOrderResponseDto(Order order, List<OrderItem> orderItems) {
        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .totalAmount(order.getTotalAmount())
                .shippingFee(order.getShippingFee())
                .status(order.getStatus())
                .orderedAt(order.getOrderedAt())
                .orderItems(orderItems.stream()
                        .map(this::toOrderItemResponseDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private OrderItemResponseDto toOrderItemResponseDto(OrderItem item) {
        return OrderItemResponseDto.builder()
                .orderItemId(item.getOrderItemId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productPrice(item.getProductPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    private PaymentResponseDto toPaymentResponseDto(Payment payment) {
        return PaymentResponseDto.builder()
                .paymentId(payment.getPaymentId())
                .paymentKey(payment.getPaymentKey())
                .method(payment.getMethod())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paidAt(payment.getPaidAt())
                .build();
    }

    private DeliveryAddressResponseDto toDeliveryAddressResponseDto(DeliveryAddress address) {
        return DeliveryAddressResponseDto.builder()
                .addressId(address.getAddressId())
                .recipientName(address.getRecipientName())
                .phone(address.getPhone())
                .zipCode(address.getZipCode())
                .address(address.getAddress())
                .addressDetail(address.getAddressDetail())
                .isDefault(address.isDefault())
                .build();
    }
}