package co.kr.allpick.domain.order.dto;

import java.math.BigDecimal;

import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.entity.OrderItem;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.entity.ProductOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 상품 요청 DTO")
public class OrderItemRequestDto {

    @NotNull
    @Schema(description = "상품 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    @NotNull
    @Schema(description = "상품 옵션 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long optionId;

    @NotNull
    @Schema(description = "수량", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    public OrderItem toEntity(Product product, ProductOption option, Order order) {
        BigDecimal price = product.getPrice();

        return OrderItem.builder()
                .order(order)
                .product(product)
                .productOption(option)
                .productName(product.getProductName())
                .productPrice(price)
                .quantity(this.quantity)
                .build();
    }
}