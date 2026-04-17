package co.kr.allpick.domain.order.dto;

import co.kr.allpick.domain.order.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    public Order toEntity(Long memberId, String orderNumber) {
        return Order.builder()
                .memberId(memberId)
                .addressId(this.addressId)
                .orderNumber(orderNumber)
                .totalAmount(BigDecimal.ZERO)
                .shippingFee(3000)
                .status(Order.OrderStatus.PENDING)
                .orderedAt(LocalDateTime.now())
                .build();
    }
}