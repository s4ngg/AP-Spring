
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
public class CartItemResponseDto {
	
	@Schema(description = "장바구니 아이템 ID", example = "1")
    private Long cartItemId;
	
    @Schema(description = "브랜드명", example = "올픽 오리지널")
    private String brandName;
	
    @Schema(description = "상품명", example = "오버핏 코튼 티셔츠")
    private String productName;
	
    @Schema(description = "상품 가격", example = "29000")
    private Integer price;
	
    @Schema(description = "선택한 옵션 정보", example = "L / White")
    private String option;
	
    @Schema(description = "담은 수량", example = "2")
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
