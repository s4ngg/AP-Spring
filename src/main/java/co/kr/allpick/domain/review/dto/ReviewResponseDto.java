package co.kr.allpick.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReviewResponseDto {
	@NotNull
    @Schema(description = "리뷰 ID", example = "1",requiredMode = RequiredMode.REQUIRED)
    private Long reviewId;
	@NotBlank
    @Schema(description = "작성자 이름", example = "홍길동",requiredMode = RequiredMode.REQUIRED)
    private String writerName;
	@NotBlank
    @Schema(description = "상품 이름", example = "남성용 오버핏 후드티",requiredMode = RequiredMode.REQUIRED)
    private String productName;
	@NotBlank
    @Schema(description = "선택한 옵션", example = "블랙 / L",requiredMode = RequiredMode.REQUIRED)
    private String selectedOption;
    @NotNull
    @Schema(description = "별점", example = "5",requiredMode = RequiredMode.REQUIRED)
    private Integer rating;
    @NotBlank
    @Schema(description = "리뷰 본문", example = "정말 마음에 들어요! 추천합니다.",requiredMode = RequiredMode.REQUIRED)
    private String content;
}
