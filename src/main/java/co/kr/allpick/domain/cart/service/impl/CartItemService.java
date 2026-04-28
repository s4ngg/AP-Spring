
package co.kr.allpick.domain.cart.service.impl;

import java.util.Optional;

import co.kr.allpick.domain.cart.dto.CartItemReqDto;
import co.kr.allpick.domain.cart.dto.CartItemResDto;
import co.kr.allpick.domain.member.entity.Member;

public interface CartItemService {
	// 장바구니에 상품 추가
	CartItemResDto addCart(CartItemReqDto reqDto, Member member);
	// 장바구니 상품 조회
	Optional<CartItemResDto> getCartItem(CartItemReqDto reqDto, Member member);
}
