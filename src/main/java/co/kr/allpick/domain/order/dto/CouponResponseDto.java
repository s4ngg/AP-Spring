package co.kr.allpick.domain.order.dto;

import co.kr.allpick.domain.order.entity.Coupon;
import co.kr.allpick.domain.order.entity.Coupon.DiscountType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "쿠폰 응답 DTO")
public class CouponResponseDto {

    @Schema(description = "쿠폰 ID", example = "1")
    private Long couponId;

    @Schema(description = "쿠폰 코드", example = "WELCOME2026")
    private String couponCode;

    @Schema(description = "할인 유형", example = "PERCENT")
    private DiscountType discountType;

    @Schema(description = "할인값", example = "10")
    private BigDecimal discountValue;

    @Schema(description = "최소 주문금액", example = "10000")
    private BigDecimal minOrderAmount;

    @Schema(description = "최대 할인금액", example = "5000")
    private BigDecimal maxDiscount;

    @Schema(description = "만료일")
    private LocalDateTime expiredAt;

    public static CouponResponseDto from(Coupon coupon) {
        return CouponResponseDto.builder()
                .couponId(coupon.getCouponId())
                .couponCode(coupon.getCouponCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxDiscount(coupon.getMaxDiscount())
                .expiredAt(coupon.getExpiredAt())
                .build();
    }
}