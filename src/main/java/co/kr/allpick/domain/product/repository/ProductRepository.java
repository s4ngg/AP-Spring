package co.kr.allpick.domain.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.kr.allpick.domain.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	// 상품id로 조회하기 전, 1. 판매중인지, 2. 승인이 난 상품인지 검증해준다.
	@Query("SELECT p FROM Product p " +
		   "WHERE p.productId = :id " +
		   "AND p.status = 'ON_SALE' " +
		   "AND p.approvalStatus = 'APPROVED' ")
	
	Optional<Product> findValidProduct(@Param("id") Long productId);
}

  