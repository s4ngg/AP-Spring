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
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
	
}
 