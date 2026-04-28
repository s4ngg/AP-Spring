package co.kr.allpick.domain.admin.board.dto;

import co.kr.allpick.domain.admin.board.entity.Faq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "FAQ 등록 요청 DTO")
public class FaqCreateRequestDto {

    @NotNull
    @Schema(description = "FAQ 카테고리", example = "DELIVERY", requiredMode = Schema.RequiredMode.REQUIRED)
    private Faq.FaqCategory category;

    @NotBlank
    @Size(max = 200)
    @Schema(description = "질문", example = "배송은 얼마나 걸리나요?", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank
    @Schema(description = "답변", example = "평균 2~3일 소요됩니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @NotNull
    @Schema(description = "노출 순서", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private int displayOrder;

    public Faq toEntity(Long adminId) {
        return new Faq(adminId, this.category, this.title, this.content, this.displayOrder);
    }
}
