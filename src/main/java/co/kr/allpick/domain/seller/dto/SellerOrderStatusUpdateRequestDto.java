package co.kr.allpick.domain.seller.dto;

import co.kr.allpick.domain.order.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "판매자 주문 상태 변경 요청 DTO")
public class SellerOrderStatusUpdateRequestDto {

    @NotNull(message = "주문 상태는 필수입니다.")
    @Schema(description = "변경할 주문 상태 (SHIPPING / DELIVERED 만 허용)",
            example = "SHIPPING",
            requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"SHIPPING", "DELIVERED"})
    private Order.OrderStatus status;
}
