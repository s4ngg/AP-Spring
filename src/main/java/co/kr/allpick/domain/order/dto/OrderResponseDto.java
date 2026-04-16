package co.kr.allpick.domain.order.dto;

import co.kr.allpick.domain.order.entity.Order.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
}