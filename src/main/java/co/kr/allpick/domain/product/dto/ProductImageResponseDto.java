package co.kr.allpick.domain.product.dto;

import co.kr.allpick.domain.product.entity.ProductImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

@Schema(description = "상품 이미지 응답 정보") 
public class ProductImageResponseDto {
	
	@Schema(description = "상품 이미지 ID", example = "1")
    private Long productImageId;

    @Schema(description = "이미지 경로 URL", example = "https://amazonaws.com")
    private String imageUrl;

    @Schema(description = "이미지 노출 순서 (낮을수록 먼저 노출)", example = "1")
    private Integer sortOrder;

	
	public static ProductImageResponseDto from(ProductImage productImage) {
		
		return ProductImageResponseDto.builder()
				.productImageId(productImage.getProductImageId())
				.imageUrl(productImage.getImageUrl())
				.sortOrder(productImage.getSortOrder())
				.build();
	}
}




