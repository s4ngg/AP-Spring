package co.kr.allpick.domain.review.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.order.entity.OrderItem;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.domain.review.dto.ReviewRequestDto;
import co.kr.allpick.domain.review.dto.ReviewResponseDto;
import co.kr.allpick.domain.review.dto.ReviewUpdateRequestDto;
import co.kr.allpick.domain.review.entity.Review;
import co.kr.allpick.domain.review.repository.ReviewRepository;
import co.kr.allpick.domain.review.service.ReviewService;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Transactional
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
	
	@Override
	public ReviewResponseDto createReview(Long memberId ,ReviewRequestDto reqDto) {
		// 리뷰 작성 이전에, 상품주문내역이 존재하는지부터 조회.
		OrderItem orderItem = orderItemRepository.findWithOrderAndMember(reqDto.getOrderItemId())
				.orElseThrow(() -> new BusinessException(ErrorCode.ORDER_ITEM_NOT_FOUND));
		
		// jwt로부터 받은 memberId로 해당 주문내역의 구매자가 맞는지 확인
		if(!orderItem.getOrder().getMember().getId().equals(memberId)) {
			throw new BusinessException(ErrorCode.REVIEW_NOT_AUTHOR);
		}
		
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
	
	// 리뷰 수정( PatchMapping )
	@Override
	public ReviewResponseDto updateReview(Long reviewId ,Long memberId, ReviewUpdateRequestDto reqDto) {
		// 매개변수로 들어온 값들로 기존 작성자인지 확인.
		Review review = reviewRepository.findByReviewIdAndMemberId(reviewId, memberId)
				.orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_AUTHOR));
		// 수정 메서드 호출하여 변경사항 전달
		review.updateReview(reqDto);
		// 응답객체로 변환
		return ReviewResponseDto.from(review);
	}

}


