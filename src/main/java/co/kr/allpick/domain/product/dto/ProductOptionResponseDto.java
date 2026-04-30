package co.kr.allpick.domain.product.dto;

import co.kr.allpick.domain.product.entity.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter


public class ProductOptionResponseDto {
		
	private Long optionId;
	
	private String optionName;
	
	private String optionValue;

	private Integer additionalPrice;
	
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
