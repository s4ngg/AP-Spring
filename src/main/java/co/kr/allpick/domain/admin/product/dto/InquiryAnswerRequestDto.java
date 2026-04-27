package co.kr.allpick.domain.admin.product.dto;

import co.kr.allpick.domain.admin.product.entity.InquiryAnswer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "답변 요청 DTO")
public class InquiryAnswerRequestDto {

    @NotBlank
    @Schema(description = "답변 내용", example = "정 사이즈 입니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    public InquiryAnswer toEntity(Long inquiryId, Long adminId, Long sellerId) {
        return InquiryAnswer.builder()
                .inquiryId(inquiryId)
                .adminId(adminId)
                .sellerId(sellerId)
                .content(this.content)
                .build();
    }

}
