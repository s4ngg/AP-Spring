package co.kr.allpick.domain.order.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 생성 요청 DTO")
public class OrderCreateRequestDto {

    @NotNull
    @Schema(description = "배송지 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long addressId;

    @NotNull
    @Schema(description = "주문 상품 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderItemRequestDto> orderItems;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "주문 상품 요청 DTO")
    public static class OrderItemRequestDto {

        @NotNull
        @Schema(description = "상품 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long productId;

        @NotNull
        @Schema(description = "수량", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer quantity;
    }
}