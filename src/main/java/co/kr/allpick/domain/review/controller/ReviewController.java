package co.kr.allpick.domain.review.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.allpick.domain.review.dto.ReviewRequestDto;
import co.kr.allpick.domain.review.dto.ReviewResponseDto;
import co.kr.allpick.domain.review.service.ReviewService;
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
			@Valid @RequestBody ReviewRequestDto reqdto
			) {
		return ApiResponse.success("리뷰를 등록했습니다.", reviewService.createReview(reqdto));
	}
	
}
