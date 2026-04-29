package co.kr.allpick.domain.admin.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "클레임 거부 요청 DTO")
public class ClaimRejectRequestDto {

    @NotBlank
    @Schema(description = "거부 사유", example = "교환 기간이 초과되었습니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rejectReason;

}
