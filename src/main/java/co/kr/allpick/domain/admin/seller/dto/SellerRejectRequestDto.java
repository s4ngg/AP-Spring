package co.kr.allpick.domain.admin.seller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "판매자 승인 거절 요청 DTO")
public class SellerRejectRequestDto {

    @NotBlank
    @Schema(description = "거절 사유", example = "사업자등록번호 확인이 필요합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rejectReason;
}
