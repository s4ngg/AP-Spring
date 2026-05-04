package co.kr.allpick.domain.product.dto;

import java.math.BigDecimal;

import co.kr.allpick.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

@Schema(description = "상품수정 응답 DTO")
public class ProductUpdateResponseDto {
	@Schema(description = "수정된 상품 Id", example = "1")
	private Long productId;
	
	@Schema(description = "수정된 가격", example = "27000")
	private BigDecimal price;
	
	public static ProductUpdateResponseDto from(Product product) {
		return ProductUpdateResponseDto.builder()
				.productId(product.getProductId())
				.price(product.getPrice())
				.build();
	}
}
   