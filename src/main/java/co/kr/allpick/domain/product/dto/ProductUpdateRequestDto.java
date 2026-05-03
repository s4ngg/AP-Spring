package co.kr.allpick.domain.product.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

@Schema(description = "상품수정 요청 DTO")
public class ProductUpdateRequestDto {

	@NotNull @Schema(description = "수정할 상품 Id", example = "1", requiredMode = RequiredMode.REQUIRED)
	private Long productId;
	
	@Schema(description = "수정 가격", example = "27000", requiredMode = RequiredMode.REQUIRED)
	@PositiveOrZero
	private BigDecimal price; 
} 
