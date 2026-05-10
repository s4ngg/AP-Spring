package co.kr.allpick.domain.review.dto;

import java.time.format.DateTimeFormatter;

import co.kr.allpick.domain.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "리뷰 응답 정보")
public class ReviewResponseDto {
	
		@Schema(description = "리뷰 ID", example = "1")
	    private Long reviewId;
		
	    @Schema(description = "작성자 이름", example = "홍길동")
	    private String writerName;
		
	    @Schema(description = "상품명", example = "올픽 시그니처 티셔츠")
	    private String productName;
		
	    @Schema(description = "선택한 옵션", example = "L / White")
	    private String selectedOption;
	    
	    @Schema(description = "평점 (1~5)", example = "5")
	    private Integer rating;
	   
	    @Schema(description = "리뷰 내용", example = "재질이 너무 부드럽고 핏이 예뻐요!")
	    private String content;
	    
	    @Schema(description = "리뷰 날짜", example = "2025.01.16")
	    private String reviewDate;
	    
	    @Schema(description = "작성자 회원 ID", example = "1")
	    private Long memberId;
    
    public static ReviewResponseDto from(Review review) { 
    	return ReviewResponseDto.builder()
    			.reviewId(review.getReviewId())
    			.writerName(review.getOrderItem().getOrder().getMember().getName())
    			.productName(review.getOrderItem().getProduct().getProductName())
    			.selectedOption(review.getSelectedOption())
    			.rating(review.getRating())
    			.content(review.getContent())
    			.reviewDate(review.getUpdatedAt() != null ? 
    					review.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")):null)
    			.memberId(review.getOrderItem().getOrder().getMember().getId()) 
    			.build();
    }
}



 