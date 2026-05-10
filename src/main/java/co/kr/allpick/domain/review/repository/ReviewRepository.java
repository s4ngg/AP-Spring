package co.kr.allpick.domain.review.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.kr.allpick.domain.review.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 1. 상품 하나에 대한 전체 사용자의 리뷰 조회 (삭제된 리뷰 제외)
    @Query("SELECT r FROM Review r " +
               "JOIN FETCH r.orderItem oi " +
               "JOIN FETCH oi.order o " +
               "JOIN FETCH o.member m " +
               "WHERE oi.product.productId = :productId " +
               "AND r.deletedAt IS NULL")
    Page<Review> findByProductId(@Param("productId") Long productId, Pageable pageable);

    // 2. 기존 리뷰 작성 내역 확인 (삭제된 리뷰는 재작성 가능하도록 제외)
    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.orderItem.orderItemId = :orderItemId AND r.deletedAt IS NULL")
    boolean existsByOrderItemId(@Param("orderItemId") Long orderItemId);

    // 3. 리뷰 수정용 - 작성자 검증하여 단건 조회 (삭제된 리뷰 제외)
    @Query("SELECT r FROM Review r " +
           "JOIN FETCH r.orderItem oi " +
           "JOIN FETCH oi.order o " +
           "JOIN FETCH o.member m " +
           "WHERE r.reviewId = :reviewId AND m.id = :memberId AND r.deletedAt IS NULL")
    Optional<Review> findByReviewIdAndMemberId(@Param("reviewId") Long reviewId, @Param("memberId") Long memberId);
}
