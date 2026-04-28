package co.kr.allpick.domain.cart.dto;

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

@Schema(description = "장바구니 요청 Dto")
public class CartItemReqDto {
	@Schema(description = "상품 Id", example = "1")
	private Long productId;
	@Schema(description = "장바구니 상품 Id", example = "1")
	private Long CartItemId;
	@Schema(description = "사용자 수량 선택저장", example = "2")
	private Integer quantity;
	
	
	@Schema(description = "장바구니에 물건을 추가하는 메서드 ")
	public static CartItem addToCart(Product product, Cart cart, int quantity) {
		return CartItem.builder()
				.product(product)
				.cart(cart)
				.quantity(quantity)
				.build();
	}
}
