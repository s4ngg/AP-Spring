package co.kr.allpick.domain.review.entity;

import co.kr.allpick.domain.order.entity.OrderItem;
import co.kr.allpick.domain.review.dto.ReviewRequestDto;
import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    // ERD에 있는 유일한 외래키 (order_item_id)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;
    @Builder.Default
    @Column(name = "rating", nullable = false)
    private Integer rating = 5;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "selected_option")
    private String selectedOption;

    
     
    public Review createReview(OrderItem orderItem,ReviewRequestDto reqDto) {
    	return Review.builder()
    			.orderItem(orderItem)
    			.rating(reqDto.getRating())
    			.content(reqDto.getContent())
    			.selectedOption(reqDto.getSelectedOption())
    			.build();
    } 
}
