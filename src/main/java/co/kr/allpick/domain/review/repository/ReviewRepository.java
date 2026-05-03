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
public interface ReviewRepository extends JpaRepository<Review , Long>{
	
	 
	// 1. 상품하나에 대한 전체사용자의 리뷰 조회	
	@Query("SELECT r FROM Review r " +
	           "JOIN FETCH r.orderItem oi " +
	           "JOIN FETCH oi.order o " +
	           "JOIN FETCH o.member m " +
	           "WHERE oi.product.productId = :productId " ) 	
	Page<Review> findByProductId(@Param("productId") Long productId, Pageable pageable);
	
	// 2. 기존리뷰 작성 내역 확인 
	@Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.orderItem.orderItemId = :orderItemId")
	boolean existsByOrderItemId(Long orderItemId);
	
	// 3. 리뷰 수정,삭제용 리뷰 작성자 검증하여 단건의 리뷰 조회(작성자 본인인지)
	@Query( " SELECT r FROM Review r " +
			" JOIN FETCH r.orderItem oi " +
			" JOIN FETCH oi.order o " +
			" JOIN FETCH o.member m " +
			" WHERE r.reviewId = :reviewId And m.memberId = :memberId")
	Optional<Review> findByReviewIdAndMemberId(@Param("reviewId") Long reviewId,@Param("memberId") Long memberId);
	 
	 
}
  