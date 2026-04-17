package co.kr.allpick.domain.order.dto;

import java.math.BigDecimal;

import co.kr.allpick.domain.order.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "주문 상품 응답 DTO")
public class OrderItemResponseDto {

    @Schema(description = "주문 상품 ID", example = "1")
    private Long orderItemId;

    @Schema(description = "상품 ID", example = "1")
    private Long productId;

    @Schema(description = "상품명", example = "나이키 운동화")
    private String productName;

    @Schema(description = "상품 가격", example = "50000")
    private BigDecimal productPrice;

    @Schema(description = "수량", example = "2")
    private int quantity;

    @Schema(description = "소계", example = "100000")
    private BigDecimal totalPrice;
    
    public static OrderItemResponseDto from(OrderItem item) {
        return OrderItemResponseDto.builder()
                .orderItemId(item.getOrderItemId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productPrice(item.getProductPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}