package co.kr.allpick.domain.product.dto;

import co.kr.allpick.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "상품등록 응답Dto :	등록된 상품의 Id만 추출")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

public class ProductSaveResponseDto {
	@Schema(description = "등록된 상품에 배정된 Id", example = "2", requiredMode = RequiredMode.REQUIRED)
	private Long productId;
	@Schema(description = "상품 등록 메세지", example ="상품 등록에 성공했습니다.", requiredMode = RequiredMode.REQUIRED)
	private String message;
	
	public static ProductSaveResponseDto from(Product product) {
		return ProductSaveResponseDto.builder()
				.productId(product.getProductId())
				.message("상품 등록에 성공했습니다.")
				.build();
		
		
	}
}
