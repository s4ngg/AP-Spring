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
@Schema(description = "상품 옵션 요청 Dto")
public class ProductOptionSaveReqDto {
	@Schema(description = "옵션명", example = "사이즈", requiredMode = Schema.RequiredMode.REQUIRED)
	private String optionName;
	@Schema(description = "옵션 값", example = "250", requiredMode = Schema.RequiredMode.REQUIRED)
	private String optionValue;
	@Schema(description = "옵션 추가금", example = "2000", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer additionalPrice;
	@Schema(description = "재고 수량", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
 	private Integer stockQuantity;
	
	public ProductOption toEntity() {
		return ProductOption.builder()
		.optionName(this.optionName)
		.optionValue(this.optionValue)
		.additionalPrice(this.additionalPrice)
		.stockQuantity(this.stockQuantity)
		.build();
	}
}
 