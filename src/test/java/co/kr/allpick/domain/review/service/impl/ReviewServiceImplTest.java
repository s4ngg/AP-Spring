package co.kr.allpick.domain.review.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.entity.OrderItem;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.review.dto.ReviewRequestDto;
import co.kr.allpick.domain.review.dto.ReviewResponseDto;
import co.kr.allpick.domain.review.dto.ReviewUpdateRequestDto;
import co.kr.allpick.domain.review.entity.Review;
import co.kr.allpick.domain.review.repository.ReviewRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    @DisplayName("전체 리뷰 조회 성공 - 페이징 적용")
    void 전체_리뷰_조회_성공() {
        // given
        Long productId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        
        Review mockReview = mock(Review.class);
        OrderItem mockOrderItem = mock(OrderItem.class);
        Order mockOrder = mock(Order.class);
        Member mockMember = mock(Member.class);
        Product mockProduct = mock(Product.class);

        when(mockReview.getReviewId()).thenReturn(1L);
        when(mockReview.getOrderItem()).thenReturn(mockOrderItem);
        when(mockOrderItem.getOrder()).thenReturn(mockOrder);
        when(mockOrder.getMember()).thenReturn(mockMember);
        when(mockMember.getName()).thenReturn("홍길동");
        when(mockOrderItem.getProduct()).thenReturn(mockProduct);
        when(mockProduct.getProductName()).thenReturn("테스트 상품");
        when(mockReview.getRating()).thenReturn(5);
        when(mockReview.getContent()).thenReturn("좋아요");

        Page<Review> mockPage = new PageImpl<>(List.of(mockReview), pageable, 1);
        when(reviewRepository.findByProductId(productId, pageable)).thenReturn(mockPage);

        // when
        Page<ReviewResponseDto> result = reviewService.getReviewAll(productId, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getWriterName()).isEqualTo("홍길동");
        verify(reviewRepository, times(1)).findByProductId(productId, pageable);
    }

    @Test
    @DisplayName("리뷰 작성 성공")
    void 리뷰_작성_성공() {
        // given
        Long memberId = 1L;
        ReviewRequestDto request = ReviewRequestDto.builder()
                .orderItemId(10L)
                .rating(5)
                .content("만족합니다!")
                .build();

        OrderItem mockOrderItem = mock(OrderItem.class);
        Order mockOrder = mock(Order.class);
        Member mockMember = mock(Member.class);
        Product mockProduct = mock(Product.class);

        when(orderItemRepository.findWithOrderAndMember(request.getOrderItemId())).thenReturn(Optional.of(mockOrderItem));
        when(mockOrderItem.getOrder()).thenReturn(mockOrder);
        when(mockOrder.getMember()).thenReturn(mockMember);
        when(mockMember.getId()).thenReturn(memberId);
        when(mockMember.getName()).thenReturn("홍길동");
        when(mockOrderItem.getProduct()).thenReturn(mockProduct);
        when(mockProduct.getProductName()).thenReturn("테스트 상품");
        when(reviewRepository.existsByOrderItemId(anyLong())).thenReturn(false);

        // when
        ReviewResponseDto result = reviewService.createReview(memberId, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("만족합니다!");
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 수정 성공 - 변경 감지 확인")
    void 리뷰_수정_성공() {
        // given
        Long reviewId = 1L;
        Long memberId = 1L;
        
        // 빌더 패턴 사용하여 생성자 에러 해결
        ReviewUpdateRequestDto updateDto = ReviewUpdateRequestDto.builder()
                .rating(4)
                .content("수정된 내용")
                .build();
        
        Review mockReview = mock(Review.class);
        OrderItem mockOrderItem = mock(OrderItem.class);
        Order mockOrder = mock(Order.class);
        Member mockMember = mock(Member.class);
        Product mockProduct = mock(Product.class);

        when(reviewRepository.findByReviewIdAndMemberId(reviewId, memberId)).thenReturn(Optional.of(mockReview));
        when(mockReview.getOrderItem()).thenReturn(mockOrderItem);
        when(mockOrderItem.getOrder()).thenReturn(mockOrder);
        when(mockOrder.getMember()).thenReturn(mockMember);
        when(mockMember.getName()).thenReturn("홍길동");
        when(mockOrderItem.getProduct()).thenReturn(mockProduct);
        when(mockProduct.getProductName()).thenReturn("테스트 상품");

        // when
        ReviewResponseDto result = reviewService.updateReview(reviewId, memberId, updateDto);

        // then
        verify(mockReview, times(1)).updateReview(updateDto);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 작성자가 아니거나 리뷰 없음")
    void 리뷰_수정_실패_권한없음() {
        // given
        Long reviewId = 1L;
        Long memberId = 999L;
        
        // 빌더 패턴 사용
        ReviewUpdateRequestDto updateDto = ReviewUpdateRequestDto.builder()
                .rating(4)
                .content("수정 시도 내용")
                .build();

        when(reviewRepository.findByReviewIdAndMemberId(reviewId, memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.updateReview(reviewId, memberId, updateDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.REVIEW_NOT_AUTHOR.getMessage());
    }

    @Test
    @DisplayName("리뷰 작성 실패 - 구매자 본인이 아님")
    void 리뷰_작성_실패_본인아님() {
        // given
        Long loginMemberId = 1L;
        Long buyerId = 2L;
        ReviewRequestDto request = ReviewRequestDto.builder().orderItemId(10L).build();

        OrderItem mockOrderItem = mock(OrderItem.class);
        Order mockOrder = mock(Order.class);
        Member mockBuyer = mock(Member.class);

        when(orderItemRepository.findWithOrderAndMember(10L)).thenReturn(Optional.of(mockOrderItem));
        when(mockOrderItem.getOrder()).thenReturn(mockOrder);
        when(mockOrder.getMember()).thenReturn(mockBuyer);
        when(mockBuyer.getId()).thenReturn(buyerId);

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(loginMemberId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.REVIEW_NOT_AUTHOR.getMessage());
    }
}