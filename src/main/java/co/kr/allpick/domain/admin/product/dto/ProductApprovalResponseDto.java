package co.kr.allpick.domain.admin.product.dto;

import co.kr.allpick.domain.admin.product.entity.ProductApproval;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "상품 승인 처리 응답 DTO")
public class ProductApprovalResponseDto {

    @Schema(description = "상품 승인 이력 ID", example = "1")
    private Long productApprovalId;

    @Schema(description = "상품 ID", example = "1")
    private Long productId;

    @Schema(description = "처리 관리자 ID", example = "1")
    private Long adminId;

    @Schema(description = "요청 유형", example = "REGISTER")
    private ProductApproval.RequestType requestType;

    @Schema(description = "승인 처리 상태", example = "APPROVED")
    private ProductApproval.ApprovalStatus status;

    @Schema(description = "거절 사유", example = "상품 설명 보완이 필요합니다.")
    private String rejectReason;

    @Schema(description = "처리 일시")
    private LocalDateTime processedAt;

    public static ProductApprovalResponseDto from(ProductApproval productApproval) {
        return ProductApprovalResponseDto.builder()
                .productApprovalId(productApproval.getProductApprovalId())
                .productId(productApproval.getProduct().getProductId())
                .adminId(productApproval.getAdmin().getAdminId())
                .requestType(productApproval.getRequestType())
                .status(productApproval.getStatus())
                .rejectReason(productApproval.getRejectReason())
                .processedAt(productApproval.getProcessedAt())
                .build();
    }
}
