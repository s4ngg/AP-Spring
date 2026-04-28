package co.kr.allpick.domain.seller.product.repository;

import co.kr.allpick.domain.seller.product.entity.SellerProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SellerProductDetailRepository extends JpaRepository<SellerProductDetail, Long> {

    // 상품의 모든 상세정보 조회
    List<SellerProductDetail> findByProductId(Long productId);

    // 특정 키로 조회 (예: "원산지")
    Optional<SellerProductDetail> findByProductIdAndDetailKey(Long productId, String detailKey);

    // 상품의 모든 상세정보 삭제
    void deleteByProductId(Long productId);
}