package co.kr.allpick.domain.product.entity;

import co.kr.allpick.global.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GenerationType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "child_categories")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChildCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long childCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    private ParentCategory parentCategory;

    @Column(name = "category_name", length = 50, nullable = false)
    private String categoryName;

    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Integer isActive = 0;

    @Column(name = "slug", length = 100, nullable = false, unique = true)
    private String slug;

    public void update(ParentCategory parentCategory, String categoryName, String slug, Integer sortOrder, Integer isActive) {
        this.parentCategory = parentCategory;
        this.categoryName = categoryName;
        this.slug = slug;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
    }

    private static final String DELETED_SLUG_MARKER = "_deleted_";
    private static final int SLUG_MAX_LENGTH = 100;

    public void deactivate() {
        if (this.getDeletedAt() != null) return;
        this.isActive = 0;
        this.slug = generateDeletedSlug(this.slug);
        delete();
    }

    private String generateDeletedSlug(String originalSlug) {
        String marker = DELETED_SLUG_MARKER + this.childCategoryId + "_" + System.currentTimeMillis();
        int maxOriginalLength = SLUG_MAX_LENGTH - marker.length();
        String truncated = originalSlug.length() > maxOriginalLength
                ? originalSlug.substring(0, maxOriginalLength)
                : originalSlug;
        return truncated + marker;
    }
}
