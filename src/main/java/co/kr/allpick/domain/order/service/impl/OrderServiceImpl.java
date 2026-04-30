package co.kr.allpick.domain.order.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;

    @Override
    @Transactional
    public OrderResponseDto createOrder(Long memberId, OrderCreateRequestDto request) {
        logger.info("주문 생성 요청 - memberId: {}", memberId);

        // 1. 배송지 확인
        deliveryAddressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));

        // 2. 주문 생성
        String orderNumber = generateUniqueOrderNumber();
        Order savedOrder = orderRepository.save(request.toEntity(memberId, orderNumber));

        // 3. 주문 상품 생성
        List<OrderItem> orderItems = request.getOrderItems().stream()
                .map(item -> orderItemRepository.save(item.toEntity(savedOrder)))
                .toList();

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
        if (orderRepository.existsByAddressId(addressId)) {
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
