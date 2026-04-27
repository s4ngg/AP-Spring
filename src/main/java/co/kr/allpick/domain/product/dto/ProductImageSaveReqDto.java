package co.kr.allpick.domain.product.dto;

import co.kr.allpick.domain.product.entity.ProductImage;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "상품 이미지 등록 요청 DTO")
public class ProductImageSaveReqDto {
	@NotBlank
	@Schema(description = "상품 이미지 url" , requiredMode = Schema.RequiredMode.REQUIRED )
	private String imageUrl;
	 
	@Builder.Default
	@NotNull 
	@Schema(name = "sort_order" , example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer sortOrder = 0;
	
	// 상품 이미지 객체 변환 메서드
	public ProductImage toEntity() {
		return ProductImage.builder()
				.imageUrl(this.imageUrl)
				.sortOrder(this.sortOrder)
				.build();
	}
}  

