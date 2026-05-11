package co.kr.allpick.domain.order.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import co.kr.allpick.domain.coupon.entity.Coupon;
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
import co.kr.allpick.domain.order.entity.Order.OrderStatus;
import co.kr.allpick.domain.order.entity.OrderItem;
import co.kr.allpick.domain.order.entity.Payment;
import co.kr.allpick.domain.order.repository.DeliveryAddressRepository;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.domain.order.repository.OrderRepository;
import co.kr.allpick.domain.order.repository.PaymentRepository;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.entity.ProductOption;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceImplTest {

    @Mock OrderRepository orderRepository;
    @Mock OrderItemRepository orderItemRepository;
    @Mock PaymentRepository paymentRepository;
    @Mock DeliveryAddressRepository deliveryAddressRepository;
    @Mock MemberRepository memberRepository;
    @Mock ProductRepository productRepository;
    @Mock MemberCouponRepository memberCouponRepository;

    @InjectMocks
    OrderServiceImpl orderService;

    private Member mockMember;
    private DeliveryAddress mockAddress;

    @BeforeEach
    void setUp() {
        mockMember = new Member();
        ReflectionTestUtils.setField(mockMember, "id", 1L);

        mockAddress = DeliveryAddress.builder()
                .memberId(1L)
                .recipientName("테스터")
                .phone("01012345678")
                .zipCode("12345")
                .address("서울시")
                .addressDetail("101호")
                .isDefault(true)
                .build();
        ReflectionTestUtils.setField(mockAddress, "addressId", 1L);
    }

    @Test
    @DisplayName("주문 생성 성공 - 항목 및 재고 차감 확인")
    void 주문_생성_성공() {
        // given
        Long memberId = 1L;
        OrderItemRequestDto itemRequest = new OrderItemRequestDto(100L, 10L, 2);
        OrderCreateRequestDto request = new OrderCreateRequestDto(1L, null, List.of(itemRequest), 3000);

        ProductOption mockOption = spy(ProductOption.builder()
                .stockQuantity(10)
                .build());
        ReflectionTestUtils.setField(mockOption, "optionId", 10L);

        Product mockProduct = Product.builder()
                .productId(100L)
                .productName("테스트 상품")
                .optionList(List.of(mockOption))
                .build();

        Order mockOrder = spy(new Order());
        ReflectionTestUtils.setField(mockOrder, "orderId", 1L);
        ReflectionTestUtils.setField(mockOrder, "orderNumber", "ORD-GENERATED-001");
        ReflectionTestUtils.setField(mockOrder, "orderItems", new ArrayList<>());

        given(memberRepository.findById(memberId)).willReturn(Optional.of(mockMember));
        given(deliveryAddressRepository.findById(anyLong())).willReturn(Optional.of(mockAddress));
        given(orderRepository.existsByOrderNumber(anyString())).willReturn(false);
        given(orderRepository.save(any(Order.class))).willReturn(mockOrder);
        given(productRepository.findById(100L)).willReturn(Optional.of(mockProduct));
        given(orderItemRepository.sumTotalPriceByOrderId(any())).willReturn(BigDecimal.valueOf(20000));

        // when
        OrderResponseDto result = orderService.createOrder(memberId, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderNumber()).isEqualTo("ORD-GENERATED-001");
        verify(mockOption).removeStock(2);
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 생성 실패 - 상품이 존재하지 않음")
    void 주문_생성_실패_상품없음() {
        // given
        Long memberId = 1L;
        OrderItemRequestDto itemRequest = new OrderItemRequestDto(999L, 10L, 1);
        OrderCreateRequestDto request = new OrderCreateRequestDto(1L, null, List.of(itemRequest), 3000);

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(mockMember));
        given(deliveryAddressRepository.findById(anyLong())).willReturn(Optional.of(mockAddress));
        given(orderRepository.save(any())).willReturn(new Order());
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(memberId, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("주문 생성 성공 - 쿠폰 할인이 상품 금액 초과 시 0원 처리")
    void 주문_생성_성공_쿠폰할인_상품금액초과() {
        // given
        Long memberId = 1L;
        OrderItemRequestDto itemRequest = new OrderItemRequestDto(100L, 10L, 1);
        OrderCreateRequestDto request = new OrderCreateRequestDto(1L, 1L, List.of(itemRequest), 3000);

        ProductOption mockOption = spy(ProductOption.builder().stockQuantity(10).build());
        ReflectionTestUtils.setField(mockOption, "optionId", 10L);

        Product mockProduct = Product.builder()
                .productId(100L)
                .productName("테스트 상품")
                .price(BigDecimal.valueOf(10))
                .optionList(List.of(mockOption))
                .build();

        Coupon mockCoupon = mock(Coupon.class);
        given(mockCoupon.getDiscountType()).willReturn(Coupon.DiscountType.AMOUNT);
        given(mockCoupon.getDiscountValue()).willReturn(BigDecimal.valueOf(5000));
        given(mockCoupon.getMaxDiscount()).willReturn(null);
        given(mockCoupon.getMinOrderAmount()).willReturn(BigDecimal.ZERO);

        MemberCoupon mockMemberCoupon = mock(MemberCoupon.class);
        given(mockMemberCoupon.getCoupon()).willReturn(mockCoupon);
        given(mockMemberCoupon.isUsed()).willReturn(false);

        Order mockOrder = spy(new Order());
        ReflectionTestUtils.setField(mockOrder, "orderId", 1L);
        ReflectionTestUtils.setField(mockOrder, "orderNumber", "ORD-GENERATED-002");
        ReflectionTestUtils.setField(mockOrder, "orderItems", new ArrayList<>());

        given(memberRepository.findById(memberId)).willReturn(Optional.of(mockMember));
        given(deliveryAddressRepository.findById(anyLong())).willReturn(Optional.of(mockAddress));
        given(orderRepository.existsByOrderNumber(anyString())).willReturn(false);
        given(orderRepository.save(any(Order.class))).willReturn(mockOrder);
        given(productRepository.findById(100L)).willReturn(Optional.of(mockProduct));
        given(memberCouponRepository.findById(1L)).willReturn(Optional.of(mockMemberCoupon));
        given(orderItemRepository.sumTotalPriceByOrderId(any())).willReturn(BigDecimal.valueOf(10));

        // when
        OrderResponseDto result = orderService.createOrder(memberId, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderNumber()).isEqualTo("ORD-GENERATED-002");
    }

    @Test
    @DisplayName("배송지 수정 성공")
    void 배송지_수정_성공() {
        // given
        Long memberId = 1L;
        Long addressId = 1L;
        DeliveryAddressRequestDto request = new DeliveryAddressRequestDto(
                "수정된이름", "01000000000", "12345", "서울", "202", true
        );

        DeliveryAddress addressToUpdate = spy(mockAddress);

        given(deliveryAddressRepository.findById(addressId)).willReturn(Optional.of(addressToUpdate));
        given(orderRepository.existsByDeliveryAddress_AddressId(addressId)).willReturn(false);

        // when
        DeliveryAddressResponseDto result = orderService.updateDeliveryAddress(memberId, addressId, request);

        // then
        assertThat(result.getRecipientName()).isEqualTo("수정된이름");
        verify(addressToUpdate).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean());
    }

    @Test
    @DisplayName("배송지 삭제 실패 - 이미 주문에 사용된 배송지")
    void 배송지_삭제_실패_사용중() {
        // given
        Long memberId = 1L;
        Long addressId = 1L;

        given(deliveryAddressRepository.findById(addressId)).willReturn(Optional.of(mockAddress));
        given(orderRepository.existsByDeliveryAddress_AddressId(addressId)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> orderService.deleteDeliveryAddress(memberId, addressId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADDRESS_CANNOT_MODIFY);
    }

    @Test
    @DisplayName("결제 조회 성공")
    void 결제_조회_성공() {
        // given
        Long orderId = 1L;
        Payment mockPayment = Payment.builder()
                .paymentId(100L)
                .amount(new BigDecimal("50000"))
                .method(Payment.PaymentMethod.CARD)
                .status(Payment.PaymentStatus.DONE)
                .build();

        given(paymentRepository.findByOrder_OrderId(orderId)).willReturn(Optional.of(mockPayment));

        // when
        PaymentResponseDto result = orderService.getPayment(orderId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("50000"));
        verify(paymentRepository).findByOrder_OrderId(orderId);
    }

    @Test
    @DisplayName("주문 취소 성공")
    void 주문_취소_성공() {
        // given
        Long memberId = 1L;
        Long orderId = 1L;
        Order order = spy(order(memberId, OrderStatus.PENDING));
        ProductOption productOption = ProductOption.builder()
                .optionName("색상")
                .optionValue("블랙")
                .stockQuantity(5)
                .build();
        Product product = Product.builder()
                .productId(100L)
                .productName("테스트 상품")
                .build();
        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .productOption(productOption)
                .productName("테스트 상품")
                .productPrice(BigDecimal.valueOf(10000))
                .quantity(2)
                .totalPrice(BigDecimal.valueOf(20000))
                .build();
        order.addOrderItem(orderItem);

        given(orderRepository.findByIdWithItems(orderId)).willReturn(Optional.of(order));

        // when
        OrderResponseDto result = orderService.cancelOrder(memberId, orderId);

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(productOption.getStockQuantity()).isEqualTo(7);
        verify(order).updateStatus(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("주문 취소 실패 - 주문이 존재하지 않음")
    void 주문_취소_실패_주문없음() {
        // given
        Long memberId = 1L;
        Long orderId = 1L;

        given(orderRepository.findByIdWithItems(orderId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(memberId, orderId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_NOT_FOUND);
    }

    @Test
    @DisplayName("주문 취소 실패 - 본인 주문이 아님")
    void 주문_취소_실패_본인주문아님() {
        // given
        Long memberId = 1L;
        Long orderId = 1L;
        Order order = order(2L, OrderStatus.PENDING);

        given(orderRepository.findByIdWithItems(orderId)).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(memberId, orderId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED_ORDER);
    }

    @Test
    @DisplayName("주문 취소 실패 - 취소 불가능한 상태")
    void 주문_취소_실패_취소불가능상태() {
        // given
        Long memberId = 1L;
        Long orderId = 1L;
        for (OrderStatus status : List.of(
                OrderStatus.PAID,
                OrderStatus.SHIPPING,
                OrderStatus.DELIVERED,
                OrderStatus.CANCELLED
        )) {
            Order order = order(memberId, status);

            given(orderRepository.findByIdWithItems(orderId)).willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.cancelOrder(memberId, orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_CANNOT_CANCEL);
        }
    }

    @Test
    @DisplayName("배송지 목록 조회 성공")
    void 배송지_목록_조회_성공() {
        // given
        Long memberId = 1L;
        given(deliveryAddressRepository.findByMemberIdAndDeletedAtIsNull(memberId))
                .willReturn(List.of(mockAddress));

        // when
        List<DeliveryAddressResponseDto> result = orderService.getDeliveryAddresses(memberId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRecipientName()).isEqualTo("테스터");
        assertThat(result.get(0).getAddressId()).isEqualTo(1L);
    }

    private Order order(Long memberId, OrderStatus status) {
        Member member = new Member();
        ReflectionTestUtils.setField(member, "id", memberId);

        Order order = Order.builder()
                .member(member)
                .deliveryAddress(mockAddress)
                .orderNumber("ORD-TEST-001")
                .totalAmount(BigDecimal.valueOf(10000))
                .discountAmount(BigDecimal.ZERO)
                .shippingFee(3000)
                .status(status)
                .orderedAt(java.time.LocalDateTime.now())
                .build();

        ReflectionTestUtils.setField(order, "orderId", 1L);
        ReflectionTestUtils.setField(order, "orderItems", new ArrayList<>());
        return order;
    }
}