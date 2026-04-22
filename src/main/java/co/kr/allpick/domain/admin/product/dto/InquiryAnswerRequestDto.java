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

    @Schema(description = "관리자 ID", example = "1")
    private Long adminId;

    @Schema(description = "판매자 ID", example = "1")
    private Long sellerId;

    @NotBlank
    @Schema(description = "답변 내용", example = "정 사이즈 입니다.")
    private String content;

    public InquiryAnswer toEntity(Long inquiryId) {
        return InquiryAnswer.builder()
                .inquiryId(inquiryId)
                .adminId(this.adminId)
                .sellerId(this.sellerId)
                .content(this.content)
                .build();
    }

}
