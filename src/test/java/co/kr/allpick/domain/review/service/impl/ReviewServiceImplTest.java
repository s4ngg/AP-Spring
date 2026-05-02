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
        Pageable pageable = PageRequest.of(0, 5); // 페이징 객체 생성
        
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

        // List 대신 Page 객체로 래핑
        Page<Review> mockPage = new PageImpl<>(List.of(mockReview), pageable, 1);
        when(reviewRepository.findByProductId(productId, pageable)).thenReturn(mockPage);

        // when
        Page<ReviewResponseDto> result = reviewService.getReviewAll(productId, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getWriterName()).isEqualTo("홍길동");
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(reviewRepository, times(1)).findByProductId(productId, pageable);
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

        OrderItem mockOrderItem = mock(OrderItem.class);
        Order mockOrder = mock(Order.class);
        Member mockMember = mock(Member.class);
        Product mockProduct = mock(Product.class);

        when(orderItemRepository.findById(request.getOrderItemId())).thenReturn(Optional.of(mockOrderItem));
        when(reviewRepository.existsByOrderItemId(request.getOrderItemId())).thenReturn(false); // 메서드명 수정 반영 확인 필요
        
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
        when(reviewRepository.existsByOrderItemId(1L)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.REVIEW_ALREADY_EXISTS.getMessage());
    }
}