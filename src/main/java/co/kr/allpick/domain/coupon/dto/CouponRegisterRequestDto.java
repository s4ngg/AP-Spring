package co.kr.allpick.domain.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import co.kr.allpick.domain.coupon.entity.Coupon;
import co.kr.allpick.domain.coupon.entity.Coupon.DiscountType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "쿠폰 등록 요청 DTO")
public class CouponRegisterRequestDto {

    @NotBlank
    @Schema(description = "쿠폰 코드", example = "WELCOME2026", requiredMode = Schema.RequiredMode.REQUIRED)
    private String couponCode;

    @NotNull
    @Schema(description = "할인 유형", example = "PERCENT", requiredMode = Schema.RequiredMode.REQUIRED)
    private DiscountType discountType;

    @NotNull
    @Schema(description = "할인값", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal discountValue;

    @NotNull
    @Schema(description = "최소 주문금액", example = "10000", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal minOrderAmount;

    @Schema(description = "최대 할인금액", example = "5000")
    private BigDecimal maxDiscount;

    @NotNull
    @Schema(description = "만료일", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expiredAt;

    public Coupon toEntity() {
        return Coupon.builder()
                .couponCode(this.couponCode)
                .discountType(this.discountType)
                .discountValue(this.discountValue)
                .minOrderAmount(this.minOrderAmount)
                .maxDiscount(this.maxDiscount)
                .expiredAt(this.expiredAt)
                .build();
    }
}