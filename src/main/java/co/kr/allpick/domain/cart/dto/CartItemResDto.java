package co.kr.allpick.domain.cart.dto;

import co.kr.allpick.domain.cart.entity.CartItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

@Schema(description = "장바구니 상품 응답 Dto")
public class CartItemResDto {
	@Schema(description = "장바구니 상품 Id")
	private Long cartItemId;
	@Schema(description = "브랜드명", example = "나이키")
	private String brandName;
	@Schema(description = "상품명", example = "에어포스")
	private String productName;
	@Schema(description = "가격", example = "17000")
	private Integer price;
	@Schema(description = "상품옵션", example = "270")
	private String option;
	@Schema(description = "수량 선택값", example = "2")
	private Integer quantity;
	
	
	@Schema(description = "장바구니에 담은 상품을 응답dto로 변환 (화면용)")
	public static CartItemResDto from(CartItem cartItem) {
		return CartItemResDto.builder()
				.cartItemId(cartItem.getCartItemId())
				.brandName(cartItem.getProduct().getBrand())
				.productName(cartItem.getProduct().getProductName())
				.price(cartItem.getProduct().getPrice().intValue())
				.option(cartItem.getProductOption().getOptionValue())
				.quantity(cartItem.getQuantity())
				.build();
	}
}
