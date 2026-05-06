package co.kr.allpick.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReviewRequestDto {

    @NotNull @Schema(description = "주문상품Id", example = "1", requiredMode = RequiredMode.REQUIRED)
    private Long orderItemId;

    @Min(1) @Max(5)
    @NotNull @Schema(description = "별점", example = "5", requiredMode = RequiredMode.REQUIRED)
    private Integer rating; 

    @NotBlank @Schema(description = "리뷰 본문", example = "화이트 컬러라 관리가 좀 필요하지만 스타일링하기 너무 좋아요!", 	
    					requiredMode = RequiredMode.REQUIRED)
    
    private String content;
    
    @NotBlank @Schema(description = "선택한 옵션값", example = "1", requiredMode = RequiredMode.REQUIRED)
    private String selectedOption; 
}




