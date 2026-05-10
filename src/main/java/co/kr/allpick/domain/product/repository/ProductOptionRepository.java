package co.kr.allpick.domain.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.kr.allpick.domain.product.entity.ProductOption;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    // 삭제되지 않은 옵션만 조회 (장바구니 담기 시 사용)
    @Query("SELECT po FROM ProductOption po WHERE po.optionId = :optionId AND po.deletedAt IS NULL")
    Optional<ProductOption> findActiveById(@Param("optionId") Long optionId);
}
