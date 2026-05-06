package co.kr.allpick.domain.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

@Schema(description = "장바구니 상품객체 요청Dto")
public class CartItemRequestDto {
	
	@NotNull @Schema(description = "상품 Id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long productId;
	
	@NotNull @Schema(description = "상품옵션 Id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long productOptionId;
	
	@NotNull @Schema(description = "사용자 수량 선택저장", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer quantity;
	
	
	
}
