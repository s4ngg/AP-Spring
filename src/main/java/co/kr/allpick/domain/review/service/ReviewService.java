package co.kr.allpick.domain.review.service;

import java.util.List;

import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.review.dto.ReviewRequestDto;
import co.kr.allpick.domain.review.dto.ReviewResponseDto;

public interface ReviewService {
	// 전체사용자의 리뷰 조회.
	List<ReviewResponseDto> getReviewAll(Long productId);
	
	// 리뷰 작성..(Review 객체는 orderItem 객체를 매개변수로 받으므로, reqDto의 orderItemId가
	//존재하는 지 확인 하고, 새로운 리뷰객체 생성)
	ReviewResponseDto createReview(ReviewRequestDto reqdto);
}
