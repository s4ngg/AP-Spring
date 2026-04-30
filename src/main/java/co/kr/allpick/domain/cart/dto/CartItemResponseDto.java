
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
	
	private Long cartItemId;
	
	private String brandName;
	
	private String productName;
	
	private Integer price;
	
	private String option;
	
	private Integer quantity;
	
	
	
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
