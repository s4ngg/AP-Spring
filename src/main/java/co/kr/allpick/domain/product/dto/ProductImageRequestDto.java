package co.kr.allpick.domain.product.dto;

import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.entity.ProductImage;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

@Schema(description = "상품이미지 생성 요청dto")
public class ProductImageRequestDto {
	
	@Schema(description = "이미지 Id ")
	private Long productImageId;
	@NotBlank
	@Schema(name ="상세 이미지 url" , example =  "https://allpick.com", requiredMode = RequiredMode.REQUIRED)
	private String imageUrl;
	@NotNull
	@Schema(name ="이미지 노출 순서" , example = "1", requiredMode = RequiredMode.REQUIRED)
	private Integer sortOrder;
	
	@Schema(description = "상품이미지 생성 메서드 :	상품이 먼저 존재해야 사용가능")
	public ProductImage toEntity(Product product) {
		return ProductImage.builder()
				.productImageId(this.productImageId)
				.imageUrl(this.imageUrl)
				.sortOrder(this.sortOrder)
				.product(product)
				.build();
				
	}
}
