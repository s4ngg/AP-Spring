package co.kr.allpick.domain.admin.product.dto;

import co.kr.allpick.domain.admin.product.entity.Claim;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull
    @Schema(description = "사유 코드 " +
            "[반품] CHANGE_MIND=단순 변심, SIZE_COLOR=사이즈/색상 불만족, DESCRIPTION_DIFF=상품 설명과 다름 " +
            "[교환] SIZE_CHANGE=사이즈 변경, COLOR_CHANGE=색상 변경 " +
            "[공통] DEFECT=상품 불량/파손, WRONG_ITEM=오배송, MISSING_ITEM=구성품 누락, OTHER=기타",
            example = "CHANGE_MIND", requiredMode = Schema.RequiredMode.REQUIRED)
    private Claim.ReasonCode reasonCode;

    @Size(max = 500, message = "500자 이하로 작성해주세요.")
    @Schema(description = "상세 사유", example = "단순 변심으로 인한 반품 요청입니다.")
    private String detail;

    @NotNull
    @Schema(description = "수거 방식 (COURIER: 택배, VISIT: 방문)", example = "COURIER")
    private Claim.ClaimPickupMethod pickupMethod;

    @Size(max = 100)
    @Schema(description = "교환 옵션 (교환 시 원하는 옵션 입력)", example = "L사이즈 블랙")
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
