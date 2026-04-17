package co.kr.allpick.domain.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.kr.allpick.domain.cart.entity.Product;

// 상품 레포지토리
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
 
}
