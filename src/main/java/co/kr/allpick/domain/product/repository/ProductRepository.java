package co.kr.allpick.domain.product.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.seller.entity.Seller;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	// 상품id로 조회하기 전, 1. 판매중인지, 2. 승인이 난 상품인지 검증해준다.
	@Query("SELECT p FROM Product p " +
		   "LEFT JOIN FETCH p.parentCategory " +
		   "WHERE p.productId = :id " +
		   "AND p.status = 'ON_SALE' " +
		   "AND p.approvalStatus = 'APPROVED' ")
	Optional<Product> findValidProduct(@Param("id") Long productId);
	
	// 상품명 존재 여부 확인 메서드
	boolean existsByProductName(String productName);
	
	// 상품 조회 (판매자가 검증이된 경우만.)
 	@Query("SELECT p FROM Product p " + 
 		   " JOIN FETCH p.seller s " + 
 			"JOIN s.member m " + 
 		   "WHERE p.productId = :productId AND m.id = :memberId")
	Optional<Product> findByProductIdAndMemberId(@Param("memberId") Long memberId,@Param("productId") Long productId );
 	
	// 판매 중이고 승인된 상품 목록 페이지 조회
	@EntityGraph(attributePaths = {"parentCategory"})
	Page<Product> findByStatusAndApprovalStatusAndDeletedAtIsNull(
			Product.Status status,
			Product.ApprovalStatus approvalStatus,
			Pageable pageable
	);


}
