package co.kr.allpick.domain.cart.dto;

import com.sun.istack.NotNull;

import co.kr.allpick.domain.cart.entity.Cart;
import co.kr.allpick.domain.cart.entity.CartItem;
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

@Schema(description = "장바구니 상품객체 요청Dto")
public class CartItemRequestDto {
	
	@NotNull @Schema(description = "상품 Id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long productId;
	
	@NotNull @Schema(description = "장바구니 상품 Id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long cartItemId;
	
	@NotNull @Schema(description = "회원 Id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long memberId;
	
	@NotNull @Schema(description = "상품옵션 Id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long productOptionId;
	
	@NotNull @Schema(description = "사용자 수량 선택저장", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer quantity;
	
	
	
}
