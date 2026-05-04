package co.kr.allpick.domain.review.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.allpick.domain.review.dto.ReviewRequestDto;
import co.kr.allpick.domain.review.dto.ReviewResponseDto;
import co.kr.allpick.domain.review.dto.ReviewUpdateRequestDto;
import co.kr.allpick.domain.review.service.ReviewService;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
	
	private final ReviewService reviewService;
	
	@GetMapping("/{productId}")
	public ResponseEntity<ApiResponse<Page<ReviewResponseDto>>> getReviewAll(
			@PathVariable("productId") Long productId,
			@PageableDefault(size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
		return ApiResponse.success("해당 상품 리뷰를 조회합니다.",reviewService.getReviewAll(productId, pageable));
	}
	
	@PostMapping
	public ResponseEntity<ApiResponse<ReviewResponseDto>> createReview(
			@AuthenticationPrincipal JwtUserInfoDto userInfo,
			@Valid @RequestBody ReviewRequestDto reqDto
			) {
		return ApiResponse.success("리뷰를 등록했습니다.", 
				reviewService.createReview( userInfo.getMemberId(), reqDto));
	}
	
	@PatchMapping("/{reviewId}")
	public ResponseEntity<ApiResponse<ReviewResponseDto>> updateReview(
			@PathVariable("reviewId") Long reviewId,
			@AuthenticationPrincipal JwtUserInfoDto userInfo,
			@Valid @RequestBody ReviewUpdateRequestDto reqDto
			){
		return ApiResponse.success("리뷰를 수정했습니다.",reviewService.updateReview(reviewId ,userInfo.getMemberId(), reqDto));
	}  
}
  