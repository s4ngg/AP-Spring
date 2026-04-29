package co.kr.allpick.domain.product.dto;

import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.entity.ProductOption;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter

@Schema(description = "상품옵션 요청Dto")
public class ProductOptionRequestDto {
	
	@Schema(description = "옵션Id", example = "1", requiredMode = RequiredMode.REQUIRED)
	private Long optionId;
	@NotBlank
	@Schema(description = "옵션명", example = "사이즈", requiredMode = RequiredMode.REQUIRED)
	private String optionName;
	@NotBlank
	@Schema(description = "옵션값", example = "250", requiredMode = RequiredMode.REQUIRED)
	private String optionValue;
	@NotNull
	@Schema(description= "옵션 선택시의 추가금", example = "20000", requiredMode = RequiredMode.REQUIRED)
	private Integer additionalPrice;
	@NotNull
	@Schema(description = "재고수량", example = "50", requiredMode = RequiredMode.REQUIRED)
	private Integer stockQuantity;
	
	@Schema(description = "상품이 먼저 존재하고, 옵션을 연결해준다.")
	public ProductOption toEntity(Product product) {
		return ProductOption.builder()
				.product(product)
				.optionId(this.optionId)
				.optionValue(this.optionValue)
				.optionName(this.optionName)
				.additionalPrice(this.additionalPrice)
				.stockQuantity(this.stockQuantity)
				.build();
	}
}



