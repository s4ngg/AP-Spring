package co.kr.allpick.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.kr.allpick.domain.product.entity.ParentCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentCategoryRepository extends JpaRepository<ParentCategory, Long>{

    // 노출 중인 카테고리만 정렬 순서대로 조회
    List<ParentCategory> findByIsActiveOrderBySortOrderAsc(Integer isActive);

    // slug로 단건 조회
    Optional<ParentCategory> findBySlug(String slug);

}
