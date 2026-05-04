package co.kr.allpick.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "리뷰 수정 요청 정보")
public class ReviewUpdateRequestDto {
	
	@Schema(description = "수정할 리뷰 내용", example = "재질이 생각보다 더 좋아서 하나 더 구매하려고요!")
	@NotBlank(message = "내용을 입력해주세요.")
    private String content;
	 
	@Schema(description = "수정할 평점 (1~5)", example = "5")
    @Min(1) @Max(5)
    private Integer rating;
}
