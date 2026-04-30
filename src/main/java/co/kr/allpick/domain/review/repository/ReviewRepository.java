package co.kr.allpick.domain.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.kr.allpick.domain.review.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review , Long>{
	@Query("SELECT r FROM Review r " +
	           "JOIN FETCH r.orderItem oi " +
	           "JOIN FETCH oi.product p " +
	           "JOIN FETCH oi.order o " +
	           "JOIN FETCH o.member m " +
	           "WHERE p.productId = :productId " +
	           "ORDER BY r.reviewId DESC") 
	List<Review> findByProductId(@Param("productId") Long proudctId);
}
