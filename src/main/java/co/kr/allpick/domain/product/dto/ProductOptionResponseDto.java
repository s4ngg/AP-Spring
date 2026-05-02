package co.kr.allpick.domain.product.dto;

import co.kr.allpick.domain.product.entity.ProductOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

@Schema(description = "상품 옵션 응답 정보")
public class ProductOptionResponseDto {
		
	@Schema(description = "옵션 ID", example = "1")
    private Long optionId;
	
    @Schema(description = "옵션명", example = "색상")
    private String optionName;
	
    @Schema(description = "옵션값", example = "화이트")
    private String optionValue;

    @Schema(description = "추가 금액", example = "2000")
    private Integer additionalPrice;
	
    @Schema(description = "재고 수량", example = "100")
    private Integer stockQuantity;
	
	// 옵션 응답객체 변환 메서드
	public static ProductOptionResponseDto from(ProductOption productOption) {
		return ProductOptionResponseDto
				.builder()
				.optionId(productOption.getOptionId())
				.optionName(productOption.getOptionName())
				.optionValue(productOption.getOptionValue())
				.additionalPrice(productOption.getAdditionalPrice())
				.stockQuantity(productOption.getStockQuantity())
				.build();
	}
}
