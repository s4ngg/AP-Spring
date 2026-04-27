package co.kr.allpick.domain.product.entity;

import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="product_images")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProductImage extends BaseEntity{
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id" , nullable = false)
	private Long productId;									// 상품이미지 기본키
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;								// 상품 외래키
	
	@Column(name = "image_url" , length = 500 ,nullable = false)
	private String imageUrl;								// 이미지 URL
	
	@Builder.Default
	@Column(name="sort_order" , nullable = false)
	private Integer sortOrder = 0;							// 특정이미지 순서
	 
}
