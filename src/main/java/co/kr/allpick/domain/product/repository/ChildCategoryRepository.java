package co.kr.allpick.domain.product.repository;

import co.kr.allpick.domain.product.entity.ChildCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildCategoryRepository extends JpaRepository<ChildCategory, Long> {

    // 특정 부모 카테고리의 노출 중인 자식 카테고리 정렬 순서대로 조회
    List<ChildCategory> findByParentCategory_ParentCategoryIdAndIsActiveOrderBySortOrderAsc(
            Long parentCategoryId, Integer isActive);

    List<ChildCategory> findByParentCategory_ParentCategoryIdAndIsActiveAndDeletedAtIsNullOrderBySortOrderAsc(
            Long parentCategoryId, Integer isActive);

    List<ChildCategory> findByParentCategory_ParentCategoryIdAndDeletedAtIsNullOrderBySortOrderAsc(
            Long parentCategoryId);

    // slug로 단건 조회
    Optional<ChildCategory> findBySlug(String slug);

    Optional<ChildCategory> findBySlugAndIsActiveAndDeletedAtIsNull(String slug, Integer isActive);

    Optional<ChildCategory> findByChildCategoryIdAndDeletedAtIsNull(Long childCategoryId);

    boolean existsBySlugAndDeletedAtIsNull(String slug);
}
