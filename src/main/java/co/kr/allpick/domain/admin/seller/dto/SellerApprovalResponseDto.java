package co.kr.allpick.domain.admin.seller.dto;

import co.kr.allpick.domain.admin.seller.entity.SellerApproval;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "판매자 승인 처리 응답 DTO")
public class SellerApprovalResponseDto {

    @Schema(description = "판매자 승인 이력 ID", example = "1")
    private Long sellerApprovalId;

    @Schema(description = "판매자 ID", example = "1")
    private Long sellerId;

    @Schema(description = "처리 관리자 ID", example = "1")
    private Long adminId;

    @Schema(description = "요청 유형", example = "REGISTER")
    private SellerApproval.RequestType requestType;

    @Schema(description = "승인 처리 상태", example = "APPROVED")
    private SellerApproval.ApprovalStatus status;

    @Schema(description = "거절 사유", example = "사업자등록번호 확인이 필요합니다.")
    private String rejectReason;

    @Schema(description = "처리 일시")
    private LocalDateTime processedAt;

    public static SellerApprovalResponseDto from(SellerApproval sellerApproval) {
        return SellerApprovalResponseDto.builder()
                .sellerApprovalId(sellerApproval.getSellerApprovalId())
                .sellerId(sellerApproval.getSeller().getSellerId())
                .adminId(sellerApproval.getAdmin().getAdminId())
                .requestType(sellerApproval.getRequestType())
                .status(sellerApproval.getStatus())
                .rejectReason(sellerApproval.getRejectReason())
                .processedAt(sellerApproval.getProcessedAt())
                .build();
    }
}
