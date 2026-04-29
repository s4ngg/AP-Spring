
package co.kr.allpick.domain.cart.dto;

import com.sun.istack.NotNull;

import co.kr.allpick.domain.cart.entity.CartItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

@Schema(description = "장바구니 상품 응답 Dto")
public class CartItemResponseDto {
	@NotNull @Schema(description = "장바구니 상품 Id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long cartItemId;
	@NotBlank @Schema(description = "브랜드명", example = "나이키", requiredMode = Schema.RequiredMode.REQUIRED)
	private String brandName;
	@NotBlank @Schema(description = "상품명", example = "에어포스", requiredMode = Schema.RequiredMode.REQUIRED)
	private String productName;
	@NotNull @Schema(description = "가격", example = "17000", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer price;
	@NotBlank @Schema(description = "상품옵션", example = "270", requiredMode = Schema.RequiredMode.REQUIRED)
	private String option;
	@NotNull @Schema(description = "수량 선택값", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer quantity;
	
	
	@Schema(description = "장바구니에 담은 상품을 응답dto로 변환 (화면용)")
	public static CartItemResponseDto from(CartItem cartItem) {
		return CartItemResponseDto.builder()
				.cartItemId(cartItem.getCartItemId())
				.brandName(cartItem.getProduct().getBrand())
				.productName(cartItem.getProduct().getProductName())
				.price(cartItem.getProduct().getPrice().intValue())
				.option(cartItem.getProductOption().getOptionValue())
				.quantity(cartItem.getQuantity())
				.build();
	}
}
