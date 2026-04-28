package co.kr.allpick.domain.seller.product.repository;

import co.kr.allpick.domain.seller.product.entity.SellerProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SellerProductImageRepository extends JpaRepository<SellerProductImage, Long> {

    // 상품별 이미지 조회
    List<SellerProductImage> findByProductId(Long productId);

    // 상품별 이미지 타입으로 조회
    List<SellerProductImage> findByProductIdAndImageType(Long productId, SellerProductImage.ImageType imageType);

    // 상품 이미지 전체 삭제
    void deleteByProductId(Long productId);
}