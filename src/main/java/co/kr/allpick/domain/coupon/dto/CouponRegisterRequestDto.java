package co.kr.allpick.domain.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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
    @DecimalMin(value = "0.01", message = "할인값은 0보다 커야 합니다.")
    @Schema(description = "할인값 (PERCENT: 1~100, AMOUNT: 양수)", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal discountValue;

    @NotNull
    @DecimalMin(value = "0.01", message = "최소 주문금액은 0보다 커야 합니다.")
    @Schema(description = "최소 주문금액", example = "10000", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal minOrderAmount;

    @DecimalMin(value = "0.01", message = "최대 할인금액은 0보다 커야 합니다.")
    @Schema(description = "최대 할인금액", example = "5000")
    private BigDecimal maxDiscount;

    @NotNull
    @Future(message = "만료일은 현재 시간 이후여야 합니다.")
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