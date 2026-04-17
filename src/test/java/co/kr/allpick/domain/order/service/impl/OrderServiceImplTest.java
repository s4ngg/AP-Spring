package co.kr.allpick.domain.order.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.kr.allpick.domain.order.dto.DeliveryAddressRequestDto;
import co.kr.allpick.domain.order.dto.DeliveryAddressResponseDto;
import co.kr.allpick.domain.order.dto.OrderCreateRequestDto;
import co.kr.allpick.domain.order.dto.OrderItemRequestDto;
import co.kr.allpick.domain.order.dto.OrderResponseDto;
import co.kr.allpick.domain.order.entity.DeliveryAddress;
import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.entity.OrderItem;
import co.kr.allpick.domain.order.repository.DeliveryAddressRepository;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.domain.order.repository.OrderRepository;
import co.kr.allpick.domain.order.repository.PaymentRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderItemRepository orderItemRepository;

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    DeliveryAddressRepository deliveryAddressRepository;

    @InjectMocks
    OrderServiceImpl orderService;

    @Test
    @DisplayName("주문 생성 성공")
    void 주문_생성_성공() {
        // given
        Long memberId = 1L;
        Long addressId = 1L;

        OrderItemRequestDto itemRequest = new OrderItemRequestDto(1L, 2);
        OrderCreateRequestDto request = new OrderCreateRequestDto(addressId, List.of(itemRequest));

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

        when(deliveryAddressRepository.findById(addressId)).thenReturn(Optional.of(mockAddress));
        when(orderRepository.existsByOrderNumber(any())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(mockOrderItem);

        // when
        OrderResponseDto result = orderService.createOrder(memberId, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderNumber()).isEqualTo("ORD-20260416-000001");
        assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
    }

    @Test
    @DisplayName("주문 생성 실패 - 배송지 없음")
    void 주문_생성_실패_배송지없음() {
        // given
        OrderItemRequestDto itemRequest = new OrderItemRequestDto(1L, 2);
        OrderCreateRequestDto request = new OrderCreateRequestDto(999L, List.of(itemRequest));

        when(deliveryAddressRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ADDRESS_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("주문 조회 실패 - 존재하지 않는 주문")
    void 주문_조회_실패_존재하지않는주문() {
        // given
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.getOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ORDER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("배송지 추가 성공")
    void 배송지_추가_성공() {
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

        when(deliveryAddressRepository.save(any(DeliveryAddress.class))).thenReturn(mockAddress);

        // when
        DeliveryAddressResponseDto result = orderService.addDeliveryAddress(memberId, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRecipientName()).isEqualTo("홍길동");
        assertThat(result.getPhone()).isEqualTo("010-1234-5678");
    }

    @Test
    @DisplayName("배송지 목록 조회 성공")
    void 배송지_목록_조회_성공() {
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

        when(deliveryAddressRepository.findByMemberId(memberId)).thenReturn(List.of(mockAddress));

        // when
        List<DeliveryAddressResponseDto> result = orderService.getDeliveryAddresses(memberId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRecipientName()).isEqualTo("홍길동");
    }
}