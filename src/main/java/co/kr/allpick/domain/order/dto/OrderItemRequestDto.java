package co.kr.allpick.domain.order.dto;

import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 상품 요청 DTO")
public class OrderItemRequestDto {

	@NotNull
	@Schema(description = "상품 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long productId;

    @NotNull
    @Schema(description = "수량", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    public OrderItem toEntity(Order order) {
        BigDecimal price = BigDecimal.valueOf(10000); // 추후 ProductService 연동
        return OrderItem.builder()
                .order(order)
                .productId(this.productId)
                .productName("상품명") // 추후 ProductService 연동
                .productPrice(price)
                .quantity(this.quantity)
                .totalPrice(price.multiply(BigDecimal.valueOf(this.quantity)))
                .build();
    }
}