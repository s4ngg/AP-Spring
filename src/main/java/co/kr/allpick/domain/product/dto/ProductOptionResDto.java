package co.kr.allpick.domain.product.dto;

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
@NoArgsConstructor
@AllArgsConstructor
@Getter

@Schema(description = "상품옵션 응답 DTO")
public class ProductOptionResDto {
	@NotNull
	@Schema(description = "옵션Id", example = "1", requiredMode = RequiredMode.REQUIRED)
	private Long optionId;
	@NotBlank
	@Schema(description = "옵션명", example = "사이즈", requiredMode = RequiredMode.REQUIRED)
	private String optionName;
	@NotBlank
	@Schema(description = "옵션값", example = "250", requiredMode = RequiredMode.REQUIRED)
	private String optionValue;
	@NotNull
	@Schema(description= "옵션 선택시의 추가금", example = "", requiredMode = RequiredMode.REQUIRED)
	private Integer additionalPrice;
	@NotNull
	@Schema(description = "재고수량", example = "50", requiredMode = RequiredMode.REQUIRED)
	private Integer stockQuantity;
	
	// 옵션 응답객체 변환 메서드
	public static ProductOptionResDto from(ProductOption option) {
		return ProductOptionResDto
				.builder()
				.optionId(option.getOptionId())
				.optionName(option.getOptionName())
				.optionValue(option.getOptionValue())
				.additionalPrice(option.getAdditionalPrice())
				.stockQuantity(option.getStockQuantity())
				.build();
	}
}
