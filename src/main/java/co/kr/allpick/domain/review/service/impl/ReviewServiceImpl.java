package co.kr.allpick.domain.review.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.order.entity.OrderItem;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.domain.review.dto.ReviewRequestDto;
import co.kr.allpick.domain.review.dto.ReviewResponseDto;
import co.kr.allpick.domain.review.entity.Review;
import co.kr.allpick.domain.review.repository.ReviewRepository;
import co.kr.allpick.domain.review.service.ReviewService;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService{

	private final OrderItemRepository orderItemRepository;
	private final ReviewRepository reviewRepository;
	// 전체사용자의 리뷰 조회 ( GetMapping )
	@Transactional(readOnly = true)
	@Override
	public Page<ReviewResponseDto> getReviewAll(Long productId, Pageable pageable) {
		
		// 상품에 대해 작성된 모든 리뷰를 조회
		
		
		return reviewRepository.findByProductId(productId ,pageable)
				.map(productReview -> ReviewResponseDto.from(productReview));
	}

	// 리뷰 작성 ( PostMapping )
	//(Review 객체는 orderItem 객체를 매개변수로 받으므로, reqDto의 orderItemId가
	//존재하는 지 확인 하고, 새로운 리뷰객체 생성)
	@Transactional
	@Override
	public ReviewResponseDto createReview(ReviewRequestDto reqDto) {
		// 리뷰 작성 이전에, 상품주문내역이 존재하는지부터 조회.
		OrderItem orderItem = orderItemRepository.findById(reqDto.getOrderItemId())
				.orElseThrow(() -> new BusinessException(ErrorCode.ORDER_ITEM_NOT_FOUND));
		
		// 해당 상품에 대해 리뷰를 작성한 이력이 있는지 확인
		boolean reviewExist = reviewRepository.existsByOrderItemId(reqDto.getOrderItemId());
		
		if(reviewExist) {
			throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
		}
		
		// 위의 검증을 모두 통과한 경우에만 리뷰를 생성 
		Review review = Review.createReview(orderItem, reqDto);
		// 생성된 리뷰 레포지토리에 저장하기 
		reviewRepository.save(review);
		// 응답객체 형태로 반환
		return ReviewResponseDto.from(review);
	} 

}


