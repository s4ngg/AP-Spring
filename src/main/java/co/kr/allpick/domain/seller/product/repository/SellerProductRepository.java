package co.kr.allpick.domain.seller.product.repository;

import co.kr.allpick.domain.seller.product.entity.SellerProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SellerProductRepository extends JpaRepository<SellerProduct, Long> {

    // 판매자별 전체 상품 조회
    List<SellerProduct> findBySellerId(Long sellerId);

    // 판매자별 카테고리로 상품 조회
    List<SellerProduct> findBySellerIdAndCategory(Long sellerId, SellerProduct.Category category);

    // 판매자별 상품명으로 검색
    List<SellerProduct> findBySellerIdAndNameContaining(Long sellerId, String name);

    // 판매자별 상태로 조회
    List<SellerProduct> findBySellerIdAndStatus(Long sellerId, SellerProduct.Status status);
}