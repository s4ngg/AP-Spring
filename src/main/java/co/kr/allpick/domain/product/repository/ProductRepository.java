package co.kr.allpick.domain.product.repository;

import co.kr.allpick.domain.product.entity.ProductList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductList, Long> {
}