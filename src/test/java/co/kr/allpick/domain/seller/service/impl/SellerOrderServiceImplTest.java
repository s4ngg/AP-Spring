package co.kr.allpick.domain.seller.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import co.kr.allpick.domain.order.repository.OrderRepository;
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

    @Mock
    private OrderRepository orderRepository;

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

    @Test
    @DisplayName("주문 상태 변경 성공 - PAID → SHIPPING")
    void updateOrderStatus_success_paidToShipping() {
        // given
        Long memberId = 1L;
        Long orderId = 100L;
        Seller seller = seller(SellerStatus.APPROVED);
        Order order = orderItem(Order.OrderStatus.PAID).getOrder();

        given(sellerRepository.findByMemberIdAndDeletedAtIsNull(eq(memberId)))
                .willReturn(Optional.of(seller));
        given(orderItemRepository.existsByOrderIdAndSellerId(eq(orderId), eq(seller.getSellerId())))
                .willReturn(true);
        given(orderRepository.findById(eq(orderId))).willReturn(Optional.of(order));

        // when
        sellerOrderService.updateOrderStatus(memberId, orderId, Order.OrderStatus.SHIPPING);

        // then
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.SHIPPING);
    }

    @Test
    @DisplayName("주문 상태 변경 성공 - SHIPPING → DELIVERED")
    void updateOrderStatus_success_shippingToDelivered() {
        // given
        Long memberId = 1L;
        Long orderId = 100L;
        Seller seller = seller(SellerStatus.APPROVED);
        Order order = orderItem(Order.OrderStatus.SHIPPING).getOrder();

        given(sellerRepository.findByMemberIdAndDeletedAtIsNull(eq(memberId)))
                .willReturn(Optional.of(seller));
        given(orderItemRepository.existsByOrderIdAndSellerId(eq(orderId), eq(seller.getSellerId())))
                .willReturn(true);
        given(orderRepository.findById(eq(orderId))).willReturn(Optional.of(order));

        // when
        sellerOrderService.updateOrderStatus(memberId, orderId, Order.OrderStatus.DELIVERED);

        // then
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("주문 상태 변경 실패 - 판매자 아님")
    void updateOrderStatus_fail_notSeller() {
        // given
        Long memberId = 1L;
        given(sellerRepository.findByMemberIdAndDeletedAtIsNull(eq(memberId)))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sellerOrderService.updateOrderStatus(memberId, 100L, Order.OrderStatus.SHIPPING))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_SELLER);

        verify(orderItemRepository, never()).existsByOrderIdAndSellerId(any(), any());
        verify(orderRepository, never()).findById(any());
    }

    @Test
    @DisplayName("주문 상태 변경 실패 - 미승인 판매자")
    void updateOrderStatus_fail_notApprovedSeller() {
        // given
        Long memberId = 1L;
        Seller pendingSeller = seller(SellerStatus.PENDING);
        given(sellerRepository.findByMemberIdAndDeletedAtIsNull(eq(memberId)))
                .willReturn(Optional.of(pendingSeller));

        // when & then
        assertThatThrownBy(() -> sellerOrderService.updateOrderStatus(memberId, 100L, Order.OrderStatus.SHIPPING))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SELLER_NOT_APPROVED);
    }

    @Test
    @DisplayName("주문 상태 변경 실패 - 본인 주문 아니거나 존재하지 않음 (ORDER_NOT_FOUND로 통일)")
    void updateOrderStatus_fail_orderNotFoundOrNotOwned() {
        // given
        Long memberId = 1L;
        Long orderId = 100L;
        Seller seller = seller(SellerStatus.APPROVED);

        given(sellerRepository.findByMemberIdAndDeletedAtIsNull(eq(memberId)))
                .willReturn(Optional.of(seller));
        given(orderItemRepository.existsByOrderIdAndSellerId(eq(orderId), eq(seller.getSellerId())))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> sellerOrderService.updateOrderStatus(memberId, orderId, Order.OrderStatus.SHIPPING))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_NOT_FOUND);

        verify(orderRepository, never()).findById(any());
    }

    @Test
    @DisplayName("주문 상태 변경 실패 - PENDING → SHIPPING (잘못된 전이)")
    void updateOrderStatus_fail_pendingToShipping() {
        // given
        Long memberId = 1L;
        Long orderId = 100L;
        Seller seller = seller(SellerStatus.APPROVED);
        Order order = orderItem(Order.OrderStatus.PENDING).getOrder();

        given(sellerRepository.findByMemberIdAndDeletedAtIsNull(eq(memberId)))
                .willReturn(Optional.of(seller));
        given(orderItemRepository.existsByOrderIdAndSellerId(eq(orderId), eq(seller.getSellerId())))
                .willReturn(true);
        given(orderRepository.findById(eq(orderId))).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sellerOrderService.updateOrderStatus(memberId, orderId, Order.OrderStatus.SHIPPING))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_CANNOT_SHIP);
    }

    @Test
    @DisplayName("주문 상태 변경 실패 - PAID → DELIVERED (배송 단계 건너뜀)")
    void updateOrderStatus_fail_paidToDelivered() {
        // given
        Long memberId = 1L;
        Long orderId = 100L;
        Seller seller = seller(SellerStatus.APPROVED);
        Order order = orderItem(Order.OrderStatus.PAID).getOrder();

        given(sellerRepository.findByMemberIdAndDeletedAtIsNull(eq(memberId)))
                .willReturn(Optional.of(seller));
        given(orderItemRepository.existsByOrderIdAndSellerId(eq(orderId), eq(seller.getSellerId())))
                .willReturn(true);
        given(orderRepository.findById(eq(orderId))).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sellerOrderService.updateOrderStatus(memberId, orderId, Order.OrderStatus.DELIVERED))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_CANNOT_DELIVER);
    }

    @Test
    @DisplayName("주문 상태 변경 실패 - CANCELLED 주문에서 SHIPPING 시도")
    void updateOrderStatus_fail_cancelledToShipping() {
        // given
        Long memberId = 1L;
        Long orderId = 100L;
        Seller seller = seller(SellerStatus.APPROVED);
        Order order = orderItem(Order.OrderStatus.CANCELLED).getOrder();

        given(sellerRepository.findByMemberIdAndDeletedAtIsNull(eq(memberId)))
                .willReturn(Optional.of(seller));
        given(orderItemRepository.existsByOrderIdAndSellerId(eq(orderId), eq(seller.getSellerId())))
                .willReturn(true);
        given(orderRepository.findById(eq(orderId))).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sellerOrderService.updateOrderStatus(memberId, orderId, Order.OrderStatus.SHIPPING))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_CANNOT_SHIP);
    }

    @Test
    @DisplayName("주문 상태 변경 실패 - 판매자가 트리거할 수 없는 상태(PAID/CANCELLED)로 전이 시도")
    void updateOrderStatus_fail_disallowedTargetStatus() {
        // given
        Long memberId = 1L;
        Long orderId = 100L;
        Seller seller = seller(SellerStatus.APPROVED);
        Order order = orderItem(Order.OrderStatus.PAID).getOrder();

        given(sellerRepository.findByMemberIdAndDeletedAtIsNull(eq(memberId)))
                .willReturn(Optional.of(seller));
        given(orderItemRepository.existsByOrderIdAndSellerId(eq(orderId), eq(seller.getSellerId())))
                .willReturn(true);
        given(orderRepository.findById(eq(orderId))).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sellerOrderService.updateOrderStatus(memberId, orderId, Order.OrderStatus.CANCELLED))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT);
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
