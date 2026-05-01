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

import co.kr.allpick.domain.coupon.entity.MemberCoupon;
import co.kr.allpick.domain.coupon.repository.MemberCouponRepository;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.order.dto.DeliveryAddressRequestDto;
import co.kr.allpick.domain.order.dto.DeliveryAddressResponseDto;
import co.kr.allpick.domain.order.dto.OrderCreateRequestDto;
import co.kr.allpick.domain.order.dto.OrderItemRequestDto;
import co.kr.allpick.domain.order.dto.OrderResponseDto;
import co.kr.allpick.domain.order.entity.DeliveryAddress;
import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.repository.DeliveryAddressRepository;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.domain.order.repository.OrderRepository;
import co.kr.allpick.domain.order.repository.PaymentRepository;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
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

    @Test
    @DisplayName("주문 생성 성공")
    void 주문_생성_성공() {
        Long memberId = 1L;
        Long addressId = 1L;
        Long couponId = 1L;
        Long productId = 1L;

        OrderItemRequestDto itemRequest = new OrderItemRequestDto(productId, 2);
        OrderCreateRequestDto request = new OrderCreateRequestDto(memberId, addressId, couponId, List.of(itemRequest));

        Member mockMember = Member.builder().build();
        DeliveryAddress mockAddress = DeliveryAddress.builder().build();
        MemberCoupon mockCoupon = MemberCoupon.builder().build();
        Product mockProduct = Product.builder().price(BigDecimal.valueOf(10000)).build();

        Order mockOrder = Order.builder()
            .member(mockMember)
            .memberCoupon(mockCoupon)
            .deliveryAddress(mockAddress)
            .orderNumber("ORD-001")
            .totalAmount(BigDecimal.ZERO)
            .discountAmount(BigDecimal.ZERO)
            .shippingFee(3000)
            .status(Order.OrderStatus.PENDING)
            .orderedAt(LocalDateTime.now())
            .build();

        when(memberRepository.findById(any())).thenReturn(Optional.of(mockMember));
        when(deliveryAddressRepository.findById(any())).thenReturn(Optional.of(mockAddress));
        when(memberCouponRepository.findById(any())).thenReturn(Optional.of(mockCoupon));
        when(orderRepository.existsByOrderNumber(any())).thenReturn(false);
        when(orderRepository.save(any())).thenReturn(mockOrder);
        when(productRepository.findById(any())).thenReturn(Optional.of(mockProduct));

        OrderResponseDto result = orderService.createOrder(request);

        assertThat(result).isNotNull();
        assertThat(result.getOrderNumber()).isEqualTo("ORD-001");
    }

    @Test
    @DisplayName("주문 생성 실패 - 배송지 없음")
    void 주문_생성_실패_배송지없음() {
        OrderCreateRequestDto request = new OrderCreateRequestDto(1L, 999L, 1L, List.of());

        when(memberRepository.findById(any())).thenReturn(Optional.of(Member.builder().build()));
        when(deliveryAddressRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ADDRESS_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("배송지 추가 성공")
    void 배송지_추가_성공() {
        Long memberId = 1L;
        DeliveryAddressRequestDto request = new DeliveryAddressRequestDto(
            "홍길동", "010-1234-5678", "12345", "서울", "101", false
        );

        DeliveryAddress mockAddress = DeliveryAddress.builder()
            .recipientName("홍길동")
            .build();

        when(deliveryAddressRepository.existsByMemberIdAndAddressAndAddressDetail(any(), any(), any()))
            .thenReturn(false);
        when(deliveryAddressRepository.save(any()))
            .thenReturn(mockAddress);

        DeliveryAddressResponseDto result = orderService.addDeliveryAddress(memberId, request);
        assertThat(result.getRecipientName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("배송지 목록 조회 성공")
    void 배송지_목록_조회_성공() {
        Long memberId = 1L;
        when(deliveryAddressRepository.findByMemberIdAndDeletedAtIsNull(any()))
            .thenReturn(List.of(DeliveryAddress.builder().recipientName("홍길동").build()));

        List<DeliveryAddressResponseDto> result = orderService.getDeliveryAddresses(memberId);
        assertThat(result).hasSize(1);
    }
}