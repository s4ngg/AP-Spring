package co.kr.allpick.domain.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import co.kr.allpick.domain.review.dto.ReviewRequestDto;
import co.kr.allpick.domain.review.dto.ReviewResponseDto;
import co.kr.allpick.domain.review.dto.ReviewUpdateRequestDto;

public interface ReviewService {
	// 전체사용자의 리뷰 조회.
	Page<ReviewResponseDto> getReviewAll(Long productId, Pageable pageable);
	
	// 물품을 구매한 사용자인지 검증 -> 이미 해당상품에 리뷰를 달았는지 검증 ->  리뷰 작성
	ReviewResponseDto createReview(Long memberId ,ReviewRequestDto reqDto);
	
	// 리뷰 수정
	ReviewResponseDto updateReview(Long reviewId ,Long memberId, ReviewUpdateRequestDto reqDto); 
	
}
 