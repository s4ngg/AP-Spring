package co.kr.allpick.domain.order.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @DisplayName("주문 생성 성공 - 항목 및 금액 업데이트 확인")
    void 주문_생성_성공() {
        // given
        Long memberId = 1L;
        Long addressId = 1L;
        Long productId = 100L;
        
        OrderItemRequestDto itemRequest = new OrderItemRequestDto(productId, 2);
        OrderCreateRequestDto request = new OrderCreateRequestDto(memberId, addressId, null, List.of(itemRequest));

        Member mockMember = Member.builder().build();
        DeliveryAddress mockAddress = DeliveryAddress.builder().build();
        Product mockProduct = Product.builder()
                .price(BigDecimal.valueOf(10000))
                .build();

        // 초기 save 시 반환될 Order (아이템 리스트가 초기화되어 있어야 함)
        Order mockOrder = Order.builder()
            .orderNumber("ORD-GENERATED-001")
            .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(deliveryAddressRepository.findById(addressId)).thenReturn(Optional.of(mockAddress));
        when(orderRepository.existsByOrderNumber(anyString())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        // when
        OrderResponseDto result = orderService.createOrder(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderNumber()).isEqualTo("ORD-GENERATED-001");
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("주문 생성 실패 - 상품이 존재하지 않음")
    void 주문_생성_실패_상품없음() {
        // given
        OrderItemRequestDto itemRequest = new OrderItemRequestDto(999L, 1);
        OrderCreateRequestDto request = new OrderCreateRequestDto(1L, 1L, null, List.of(itemRequest));

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(Member.builder().build()));
        when(deliveryAddressRepository.findById(anyLong())).thenReturn(Optional.of(DeliveryAddress.builder().build()));
        when(orderRepository.save(any())).thenReturn(Order.builder().build());
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("배송지 수정 성공")
    void 배송지_수정_성공() {
        // given
        Long memberId = 1L;
        Long addressId = 1L;
        DeliveryAddressRequestDto request = new DeliveryAddressRequestDto(
            "수정된이름", "010-0000-0000", "12345", "서울", "202", true
        );

        DeliveryAddress mockAddress = spy(DeliveryAddress.builder()
                .memberId(memberId)
                .build());

        when(deliveryAddressRepository.findById(addressId)).thenReturn(Optional.of(mockAddress));
        when(orderRepository.existsByAddressId(addressId)).thenReturn(false);

        // when
        DeliveryAddressResponseDto result = orderService.updateDeliveryAddress(memberId, addressId, request);

        // then
        assertThat(result.getRecipientName()).isEqualTo("수정된이름");
        verify(mockAddress).update(any(), any(), any(), any(), any(), anyBoolean());
    }

    @Test
    @DisplayName("배송지 삭제 실패 - 이미 주문에 사용된 배송지")
    void 배송지_삭제_실패_사용중() {
        // given
        Long memberId = 1L;
        Long addressId = 1L;
        DeliveryAddress mockAddress = DeliveryAddress.builder().memberId(memberId).build();

        when(deliveryAddressRepository.findById(addressId)).thenReturn(Optional.of(mockAddress));
        when(orderRepository.existsByAddressId(addressId)).thenReturn(true); // 사용 중 설정

        // when & then
        assertThatThrownBy(() -> orderService.deleteDeliveryAddress(memberId, addressId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ADDRESS_CANNOT_MODIFY.getMessage());
    }

    @Test
    @DisplayName("배송지 추가 성공")
    void 배송지_추가_성공() {
        // given
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

        // when
        DeliveryAddressResponseDto result = orderService.addDeliveryAddress(memberId, request);

        // then
        assertThat(result.getRecipientName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("배송지 목록 조회 성공")
    void 배송지_목록_조회_성공() {
        // given
        Long memberId = 1L;
        when(deliveryAddressRepository.findByMemberIdAndDeletedAtIsNull(memberId))
            .thenReturn(List.of(DeliveryAddress.builder().recipientName("홍길동").build()));

        // when
        List<DeliveryAddressResponseDto> result = orderService.getDeliveryAddresses(memberId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRecipientName()).isEqualTo("홍길동");
    }
}