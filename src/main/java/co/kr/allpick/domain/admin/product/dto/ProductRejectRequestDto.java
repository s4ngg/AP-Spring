package co.kr.allpick.domain.admin.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 승인 거절 요청 DTO")
public class ProductRejectRequestDto {

    @NotBlank
    @Schema(description = "거절 사유", example = "상품 설명 보완이 필요합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rejectReason;
}
