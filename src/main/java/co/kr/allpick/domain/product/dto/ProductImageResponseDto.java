package co.kr.allpick.domain.product.dto;

import co.kr.allpick.domain.product.entity.ProductImage;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
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
	@NotBlank
	@Schema(name ="상세 이미지 url" , example =  "https://allpick.com", requiredMode = RequiredMode.REQUIRED)
	private String imageUrl;
	@NotBlank
	@Schema(name ="이미지 노출 순서" , example = "1", requiredMode = RequiredMode.REQUIRED)
	private Integer sortOrder;
	 
	public static ProductImageResponseDto from(ProductImage productImage) {
		return ProductImageResponseDto.builder()
				.imageUrl(productImage.getImageUrl())
				.sortOrder(productImage.getSortOrder())
				.build();
	} 
}
