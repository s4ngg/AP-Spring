package co.kr.allpick.domain.admin.product.dto;

import co.kr.allpick.domain.admin.product.entity.Claim;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "클레임 등록 요청 DTO")
public class ClaimCreateRequestDto {

    @NotNull
    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @NotNull
    @Schema(description = "주문 상품 ID", example = "10")
    private Long orderItemId;

    @Schema(description = "옵션 ID", example = "5")
    private Long optionId;

    @NotNull
    @Schema(description = "클레임 유형 (EXCHANGE: 교환, RETURN: 반품)", example = "RETURN")
    private Claim.ClaimType claimType;

    @NotBlank
    @Schema(description = "사유 코드", example = "SIMPLE_CHANGE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reasonCode;

    @Schema(description = "상세 사유", example = "단순 변심으로 인한 반품 요청입니다.")
    private String detail;

    @NotNull
    @Schema(description = "수거 방식 (COURIER: 택배, VISIT: 방문)", example = "COURIER")
    private Claim.ClaimPickupMethod pickupMethod;

    @Schema(description = "교환 옵션", example = "L사이즈 블랙")
    private String exchangeOption;

    @Schema(description = "환불 금액", example = "29000")
    private BigDecimal refundAmount;

    @Schema(description = "배송비", example = "3000")
    private BigDecimal shippingFee;

    public Claim toEntity() {
        return Claim.builder()
                .memberId(this.memberId)
                .orderItemId(this.orderItemId)
                .optionId(this.optionId)
                .claimType(this.claimType)
                .reasonCode(this.reasonCode)
                .detail(this.detail)
                .pickupMethod(this.pickupMethod)
                .rejectReason(null)
                .exchangeOption(this.exchangeOption)
                .refundAmount(this.refundAmount)
                .shippingFee(this.shippingFee)
                .build();
    }
}
