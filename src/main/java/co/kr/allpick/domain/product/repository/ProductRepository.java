package co.kr.allpick.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.kr.allpick.domain.product.entity.Product;

// 상품 레포지토리
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
 
}
