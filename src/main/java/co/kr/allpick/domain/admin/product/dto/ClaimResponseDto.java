package co.kr.allpick.domain.admin.product.dto;

import co.kr.allpick.domain.admin.product.entity.Claim;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "클레임 응답 DTO")
public class ClaimResponseDto {

    @Schema(description = "클레임 ID", example = "1")
    private Long claimId;

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "주문 상품 ID", example = "10")
    private Long orderItemId;

    @Schema(description = "옵션 ID", example = "5")
    private Long optionId;

    @Schema(description = "클레임 유형", example = "RETURN")
    private Claim.ClaimType claimType;

    @Schema(description = "클레임 상태", example = "SUBMITTED")
    private Claim.ClaimStatus status;

    @Schema(description = "사유 코드", example = "SIMPLE_CHANGE")
    private String reasonCode;

    @Schema(description = "상세 사유", example = "단순 변심입니다.")
    private String detail;

    @Schema(description = "수거 방식", example = "COURIER")
    private Claim.ClaimPickupMethod pickupMethod;

    @Schema(description = "거부 사유", example = "교환 기간 초과")
    private String rejectReason;

    @Schema(description = "교환 옵션", example = "L사이즈 블랙")
    private String exchangeOption;

    @Schema(description = "환불 금액", example = "29000")
    private BigDecimal refundAmount;

    @Schema(description = "배송비", example = "3000")
    private BigDecimal shippingFee;

    @Schema(description = "클레임 등록 일시", example = "2026-04-26")
    private LocalDateTime createdAt;

    @Schema(description = "처리 완료 일시", example = "2026-04-27")
    private LocalDateTime completedAt;

    public static ClaimResponseDto from(Claim claim) {
        return ClaimResponseDto.builder()
                .claimId(claim.getClaimId())
                .memberId(claim.getMemberId())
                .orderItemId(claim.getOrderItemId())
                .optionId(claim.getOptionId())
                .claimType(claim.getClaimType())
                .status(claim.getStatus())
                .reasonCode(claim.getReasonCode())
                .detail(claim.getDetail())
                .pickupMethod(claim.getPickupMethod())
                .rejectReason(claim.getRejectReason())
                .exchangeOption(claim.getExchangeOption())
                .refundAmount(claim.getRefundAmount())
                .shippingFee(claim.getShippingFee())
                .createdAt(claim.getCreatedAt())
                .completedAt(claim.getCompletedAt())
                .build();
    }
}
