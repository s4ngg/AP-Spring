package co.kr.allpick.domain.admin.product.dto;

import co.kr.allpick.domain.admin.product.entity.Claim;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "클레임 상태 변경 요청 DTO")
public class ClaimStatusUpdateRequestDto {

    @NotNull
    @Schema(description = "관리자 완료 처리 상태", example = "COMPLETED", requiredMode = Schema.RequiredMode.REQUIRED)
    private Claim.ClaimStatus status;
}
