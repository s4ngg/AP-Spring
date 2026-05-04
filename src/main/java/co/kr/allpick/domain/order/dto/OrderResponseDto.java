package co.kr.allpick.domain.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import co.kr.allpick.domain.order.entity.Order.OrderStatus;
import co.kr.allpick.domain.order.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.entity.OrderItem;
import java.util.stream.Collectors;

@Getter
@Builder
@Schema(description = "주문 응답 DTO")
public class OrderResponseDto {

    @Schema(description = "주문 ID", example = "1")
    private Long orderId;

    @Schema(description = "주문 번호", example = "ORD-20260415-000001")
    private String orderNumber;

    @Schema(description = "총 금액", example = "50000")
    private BigDecimal totalAmount;

    @Schema(description = "배송비", example = "3000")
    private int shippingFee;

    @Schema(description = "주문 상태", example = "PENDING")
    private OrderStatus status;

    @Schema(description = "주문 일시")
    private LocalDateTime orderedAt;

    @Schema(description = "주문 상품 목록")
    private List<OrderItemResponseDto> orderItems;
    
    @Schema(description = "쿠폰 할인금액", example = "0")
    private BigDecimal discountAmount;

    @Schema(description = "회원 쿠폰 ID", example = "1")
    private Long memberCouponId;
    
    public static OrderResponseDto from(Order order, List<OrderItem> orderItems) {
        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .totalAmount(order.getTotalAmount()) 
                .discountAmount(order.getDiscountAmount())
                .memberCouponId(order.getMemberCoupon() != null ? 
                		order.getMemberCoupon().getMemberCouponId() : null)
                .shippingFee(order.getShippingFee())
                .status(order.getStatus())
                .orderedAt(order.getOrderedAt())
                .orderItems(orderItems.stream()
                        .map(OrderItemResponseDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
