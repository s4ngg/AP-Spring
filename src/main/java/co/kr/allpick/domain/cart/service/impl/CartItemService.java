
package co.kr.allpick.domain.cart.service.impl;

import java.util.List;

import co.kr.allpick.domain.cart.dto.CartItemRequestDto;
import co.kr.allpick.domain.cart.dto.CartItemResponseDto;
import co.kr.allpick.domain.member.entity.Member;

public interface CartItemService {
	// 장바구니에 상품 추가
	CartItemResponseDto addCart(CartItemRequestDto reqDto, Member member);
	// 사용자 한명이 장바구니에 담은 모든 물건 조회
	List<CartItemResponseDto> getCartItem(Long memberId);
}
