package co.kr.allpick.domain.product.dto;

import co.kr.allpick.domain.product.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter


public class ProductImageResponseDto {
	
	private Long productImageId;

	
	private String imageUrl;

	
	private Integer sortOrder;

	
	public static ProductImageResponseDto from(ProductImage productImage) {
		
		return ProductImageResponseDto.builder()
				.productImageId(productImage.getProductImageId())
				.imageUrl(productImage.getImageUrl())
				.sortOrder(productImage.getSortOrder())
				.build();
	}
}




