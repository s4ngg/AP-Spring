package co.kr.allpick.domain.product.dto;

import co.kr.allpick.domain.product.entity.ProductImage;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

@Schema(description = "상품이미지 응답 DTO")
public class ProductImageResponseDto {
	@Schema(description = "이미지 Id ", example = "1", requiredMode = RequiredMode.REQUIRED)
	private Long productImageId;

	@Schema(description = "상세 이미지 url", example = "https://allpick.com", requiredMode = RequiredMode.REQUIRED)
	private String imageUrl;

	@Schema(description = "이미지 노출 순서", example = "1", requiredMode = RequiredMode.REQUIRED)
	private Integer sortOrder;

	@Schema(description = "상품 이미지 응답객체로 변환 :	상품 생성 이후의 시점이니깐.. 상품이미지 객체를 매개변수로")
	public static ProductImageResponseDto from(ProductImage productImage) {
		
		return ProductImageResponseDto.builder()
				.productImageId(productImage.getProductImageId())
				.imageUrl(productImage.getImageUrl())
				.sortOrder(productImage.getSortOrder())
				.build();
	}
}




