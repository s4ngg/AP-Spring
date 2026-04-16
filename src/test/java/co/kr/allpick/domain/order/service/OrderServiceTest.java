package co.kr.allpick.domain.order.service;

import co.kr.allpick.domain.order.dto.DeliveryAddressRequestDto;
import co.kr.allpick.domain.order.dto.DeliveryAddressResponseDto;
import co.kr.allpick.domain.order.dto.OrderCreateRequestDto;
import co.kr.allpick.domain.order.dto.OrderResponseDto;
import co.kr.allpick.domain.order.entity.DeliveryAddress;
import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.entity.OrderItem;
import co.kr.allpick.domain.order.repository.impl.DeliveryAddressRepositoryImpl;
import co.kr.allpick.domain.order.repository.impl.OrderItemRepositoryImpl;
import co.kr.allpick.domain.order.repository.impl.OrderRepositoryImpl;
import co.kr.allpick.domain.order.repository.impl.PaymentRepositoryImpl;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepositoryImpl orderRepository;

    @Mock
    private OrderItemRepositoryImpl orderItemRepository;

    @Mock
    private PaymentRepositoryImpl paymentRepository;

    @Mock
    private DeliveryAddressRepositoryImpl deliveryAddressRepository;

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_success() {
        // given
        Long memberId = 1L;
        Long addressId = 1L;

        OrderCreateRequestDto.OrderItemRequestDto itemRequest =
                new OrderCreateRequestDto.OrderItemRequestDto(1L, 2);
        OrderCreateRequestDto request =
                new OrderCreateRequestDto(addressId, List.of(itemRequest));

        DeliveryAddress mockAddress = DeliveryAddress.builder()
                .memberId(memberId)
                .recipientName("홍길동")
                .phone("010-1234-5678")
                .zipCode("12345")
                .address("서울시 강남구")
                .addressDetail("101호")
                .isDefault(true)
                .build();

        Order mockOrder = Order.builder()
                .memberId(memberId)
                .addressId(addressId)
                .orderNumber("ORD-20260416-000001")
                .totalAmount(BigDecimal.ZERO)
                .shippingFee(3000)
                .status(Order.OrderStatus.PENDING)
                .orderedAt(LocalDateTime.now())
                .build();

        OrderItem mockOrderItem = OrderItem.builder()
                .order(mockOrder)
                .productId(1L)
                .productName("상품명")
                .productPrice(BigDecimal.valueOf(10000))
                .quantity(2)
                .totalPrice(BigDecimal.valueOf(20000))
                .build();

        given(deliveryAddressRepository.findById(addressId)).willReturn(mockAddress);
        given(orderRepository.save(any(Order.class))).willReturn(mockOrder);
        given(orderItemRepository.save(any(OrderItem.class))).willReturn(mockOrderItem);

        // when
        OrderResponseDto result = orderService.createOrder(memberId, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderNumber()).isEqualTo("ORD-20260416-000001");
        assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 조회 성공")
    void getOrder_success() {
        // given
        Long orderId = 1L;

        Order mockOrder = Order.builder()
                .memberId(1L)
                .addressId(1L)
                .orderNumber("ORD-20260416-000001")
                .totalAmount(BigDecimal.valueOf(20000))
                .shippingFee(3000)
                .status(Order.OrderStatus.PENDING)
                .orderedAt(LocalDateTime.now())
                .build();

        given(orderRepository.findById(orderId)).willReturn(mockOrder);
        given(orderItemRepository.findByOrderId(orderId)).willReturn(List.of());

        // when
        OrderResponseDto result = orderService.getOrder(orderId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderNumber()).isEqualTo("ORD-20260416-000001");
    }

    @Test
    @DisplayName("주문 조회 실패 - 존재하지 않는 주문")
    void getOrder_fail_notFound() {
        // given
        Long orderId = 999L;
        given(orderRepository.findById(orderId))
                .willThrow(new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> orderService.getOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ORDER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("배송지 추가 성공")
    void addDeliveryAddress_success() {
        // given
        Long memberId = 1L;
        DeliveryAddressRequestDto request = new DeliveryAddressRequestDto(
                "홍길동", "010-1234-5678", "12345", "서울시 강남구", "101호", false);

        DeliveryAddress mockAddress = DeliveryAddress.builder()
                .memberId(memberId)
                .recipientName("홍길동")
                .phone("010-1234-5678")
                .zipCode("12345")
                .address("서울시 강남구")
                .addressDetail("101호")
                .isDefault(false)
                .build();

        given(deliveryAddressRepository.save(any(DeliveryAddress.class))).willReturn(mockAddress);

        // when
        DeliveryAddressResponseDto result = orderService.addDeliveryAddress(memberId, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRecipientName()).isEqualTo("홍길동");
        assertThat(result.getPhone()).isEqualTo("010-1234-5678");
        verify(deliveryAddressRepository, times(1)).save(any(DeliveryAddress.class));
    }

    @Test
    @DisplayName("배송지 목록 조회 성공")
    void getDeliveryAddresses_success() {
        // given
        Long memberId = 1L;
        DeliveryAddress mockAddress = DeliveryAddress.builder()
                .memberId(memberId)
                .recipientName("홍길동")
                .phone("010-1234-5678")
                .zipCode("12345")
                .address("서울시 강남구")
                .addressDetail("101호")
                .isDefault(true)
                .build();

        given(deliveryAddressRepository.findByMemberId(memberId)).willReturn(List.of(mockAddress));

        // when
        List<DeliveryAddressResponseDto> result = orderService.getDeliveryAddresses(memberId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRecipientName()).isEqualTo("홍길동");
    }
}