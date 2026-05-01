package co.kr.allpick.domain.review.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.entity.OrderItem;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.review.dto.ReviewRequestDto;
import co.kr.allpick.domain.review.dto.ReviewResponseDto;
import co.kr.allpick.domain.review.entity.Review;
import co.kr.allpick.domain.review.repository.ReviewRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import co.kr.allpick.domain.member.entity.Member;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    @DisplayName("전체 리뷰 조회 성공 - 데이터가 있는 경우")
    void 전체_리뷰_조회_성공() {
        // given
        Long productId = 1L;
        
        // DTO의 from 메서드 내부 객체 그래프 탐색을 위한 모킹
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

        when(reviewRepository.findByProductId(productId)).thenReturn(List.of(mockReview));

        // when
        List<ReviewResponseDto> result = reviewService.getReviewAll(productId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getWriterName()).isEqualTo("홍길동");
        verify(reviewRepository, times(1)).findByProductId(productId);
    }

    @Test
    @DisplayName("리뷰 작성 성공")
    void 리뷰_작성_성공() {
        // given
        ReviewRequestDto request = ReviewRequestDto.builder()
                .orderItemId(1L)
                .rating(5)
                .content("만족합니다!")
                .selectedOption("Black / L")
                .build();

        // 연관관계 객체 모킹
        OrderItem mockOrderItem = mock(OrderItem.class);
        Order mockOrder = mock(Order.class);
        Member mockMember = mock(Member.class);
        Product mockProduct = mock(Product.class);

        when(orderItemRepository.findById(request.getOrderItemId())).thenReturn(Optional.of(mockOrderItem));
        when(reviewRepository.existsByOrderItem(request.getOrderItemId())).thenReturn(false);
        
        // 응답 DTO 생성을 위한 내부 객체 모킹
        when(mockOrderItem.getOrder()).thenReturn(mockOrder);
        when(mockOrder.getMember()).thenReturn(mockMember);
        when(mockMember.getName()).thenReturn("홍길동");
        when(mockOrderItem.getProduct()).thenReturn(mockProduct);
        when(mockProduct.getProductName()).thenReturn("테스트 상품");

        // when
        ReviewResponseDto result = reviewService.createReview(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("만족합니다!");
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 작성 실패 - 주문 내역 없음")
    void 리뷰_작성_실패_주문없음() {
        // given
        ReviewRequestDto request = ReviewRequestDto.builder().orderItemId(999L).build();
        when(orderItemRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ORDER_ITEM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("리뷰 작성 실패 - 이미 작성된 리뷰 존재")
    void 리뷰_작성_실패_중복작성() {
        // given
        ReviewRequestDto request = ReviewRequestDto.builder().orderItemId(1L).build();
        OrderItem mockOrderItem = mock(OrderItem.class);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(mockOrderItem));
        when(reviewRepository.existsByOrderItem(1L)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.REVIEW_ALREADY_EXISTS.getMessage());
    }
}