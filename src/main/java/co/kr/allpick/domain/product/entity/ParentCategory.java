package co.kr.allpick.domain.product.entity;

import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "parent_categories")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ParentCategory extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long parentCategoryId;
	
	@Column(name = "category_name", length = 50 ,nullable = false)
	private String categoryName;	// 카테고리명

	@Builder.Default
	@Column(name = "sort_order", nullable = false)
	private Integer sortOrder = 0;  	// 사용자가 보는 순서
	
	@Builder.Default
	@Column(name = "is_active", nullable = false)
	private Integer isActive = 0;	// 노출 여부 ( 0: 비노출 / 1: 노출 )
		
	@Column(name = "slug", length = 100, nullable = false, unique = true)
	private String slug;	// URL 식별자	

	public void update(String categoryName, String slug, Integer sortOrder, Integer isActive) {
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
		String marker = DELETED_SLUG_MARKER + this.parentCategoryId + "_" + System.currentTimeMillis();
		int maxOriginalLength = SLUG_MAX_LENGTH - marker.length();
		String truncated = originalSlug.length() > maxOriginalLength
				? originalSlug.substring(0, maxOriginalLength)
				: originalSlug;
		return truncated + marker;
	}

}
