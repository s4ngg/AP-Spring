package co.kr.allpick.domain.seller.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.order.entity.DeliveryAddress;
import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.entity.OrderItem;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.seller.dto.SellerOrderResponseDto;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.entity.SellerStatus;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class SellerOrderServiceImplTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private SellerOrderServiceImpl sellerOrderService;

    @Test
    @DisplayName("판매자 주문 목록 조회 성공")
    void getSellerOrders_success() {
        // given
        Long memberId = 1L;
        Seller seller = seller(SellerStatus.APPROVED);
        OrderItem orderItem = orderItem(Order.OrderStatus.PAID);

        given(sellerRepository.findByMemberIdAndDeletedAtIsNull(eq(memberId)))
                .willReturn(Optional.of(seller));
        given(orderItemRepository.findSellerOrderItems(eq(seller.getSellerId())))
                .willReturn(List.of(orderItem));

        // when
        List<SellerOrderResponseDto> result = sellerOrderService.getSellerOrders(memberId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrderNumber()).isEqualTo("ORD-20260511-000001");
        assertThat(result.get(0).getMemberName()).isEqualTo("구매자");
        assertThat(result.get(0).getProductName()).isEqualTo("올픽 립밤");
        assertThat(result.get(0).getQuantity()).isEqualTo(2);
        assertThat(result.get(0).getTotalPrice()).isEqualByComparingTo("20000");
        assertThat(result.get(0).getStatus()).isEqualTo(Order.OrderStatus.PAID);
        assertThat(result.get(0).getStatusLabel()).isEqualTo("결제완료");
        assertThat(result.get(0).getDelivery().getRecipientName()).isEqualTo("홍길동");

        verify(orderItemRepository).findSellerOrderItems(eq(seller.getSellerId()));
    }

    @Test
    @DisplayName("판매자 주문 목록 조회 성공 - 주문 없음")
    void getSellerOrders_success_empty() {
        // given
        Long memberId = 1L;
        Seller seller = seller(SellerStatus.APPROVED);

        given(sellerRepository.findByMemberIdAndDeletedAtIsNull(eq(memberId)))
                .willReturn(Optional.of(seller));
        given(orderItemRepository.findSellerOrderItems(eq(seller.getSellerId())))
                .willReturn(List.of());

        // when
        List<SellerOrderResponseDto> result = sellerOrderService.getSellerOrders(memberId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("판매자 주문 목록 조회 실패 - 판매자 아님")
    void getSellerOrders_fail_notSeller() {
        // given
        Long memberId = 1L;

        given(sellerRepository.findByMemberIdAndDeletedAtIsNull(eq(memberId)))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sellerOrderService.getSellerOrders(memberId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_SELLER);

        verify(orderItemRepository, never()).findSellerOrderItems(eq(1L));
    }

    @Test
    @DisplayName("판매자 주문 목록 조회 실패 - 승인된 판매자가 아님")
    void getSellerOrders_fail_notApprovedSeller() {
        // given
        Long memberId = 1L;
        Seller seller = seller(SellerStatus.PENDING);

        given(sellerRepository.findByMemberIdAndDeletedAtIsNull(eq(memberId)))
                .willReturn(Optional.of(seller));

        // when & then
        assertThatThrownBy(() -> sellerOrderService.getSellerOrders(memberId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SELLER_NOT_APPROVED);

        verify(orderItemRepository, never()).findSellerOrderItems(eq(seller.getSellerId()));
    }

    private Seller seller(SellerStatus status) {
        return Seller.builder()
                .sellerId(1L)
                .member(Member.createLocal("seller@test.com", "encodedPassword", "판매자", "010-1111-2222", "서울시"))
                .businessName("올픽상점")
                .businessNumber("123-45-67890")
                .representativeName("대표자")
                .status(status)
                .build();
    }

    private OrderItem orderItem(Order.OrderStatus status) {
        Member buyer = Member.createLocal("buyer@test.com", "encodedPassword", "구매자", "010-3333-4444", "서울시");
        Seller seller = seller(SellerStatus.APPROVED);
        Product product = Product.builder()
                .productId(10L)
                .seller(seller)
                .productName("올픽 립밤")
                .thumbnailUrl("https://example.com/lipbalm.jpg")
                .build();
        DeliveryAddress deliveryAddress = DeliveryAddress.builder()
                .memberId(1L)
                .recipientName("홍길동")
                .phone("010-5555-6666")
                .zipCode("12345")
                .address("서울시 강남구")
                .addressDetail("101호")
                .isDefault(false)
                .build();
        Order order = Order.builder()
                .member(buyer)
                .deliveryAddress(deliveryAddress)
                .orderNumber("ORD-20260511-000001")
                .totalAmount(BigDecimal.valueOf(20000))
                .discountAmount(BigDecimal.ZERO)
                .shippingFee(3000)
                .status(status)
                .orderedAt(LocalDateTime.of(2026, 5, 11, 10, 0))
                .build();

        return OrderItem.builder()
                .order(order)
                .product(product)
                .productName("올픽 립밤")
                .productPrice(BigDecimal.valueOf(10000))
                .quantity(2)
                .totalPrice(BigDecimal.valueOf(20000))
                .build();
    }
}
