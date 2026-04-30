package co.kr.allpick.domain.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import co.kr.allpick.domain.coupon.entity.MemberCoupon;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.order.entity.DeliveryAddress;
import co.kr.allpick.domain.order.entity.Order;
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
	@Schema(description = "회원 Id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long memberId;
	
    @NotNull
    @Schema(description = "배송지 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long addressId;

    @Schema(description = "회원 쿠폰 ID", example = "1")
    private Long memberCouponId;

    @NotNull
    @Schema(description = "주문 상품 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderItemRequestDto> orderItems;

    public Order toEntity(Member member, MemberCoupon memberCoupon, DeliveryAddress deliveryAddress, String orderNumber) {
        return Order.builder()
                .member(member)
                .memberCoupon(memberCoupon)
                .deliveryAddress(deliveryAddress)
                .orderNumber(orderNumber)
                .totalAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .shippingFee(3000)
                .status(Order.OrderStatus.PENDING)
                .orderedAt(LocalDateTime.now())
                .build();
    }
}