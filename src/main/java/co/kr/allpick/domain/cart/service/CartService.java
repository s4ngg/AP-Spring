
package co.kr.allpick.domain.cart.service;

import java.util.List;

import co.kr.allpick.domain.cart.dto.CartItemRequestDto;
import co.kr.allpick.domain.cart.dto.CartItemResponseDto;

public interface CartService {
	// 장바구니에 상품 추가
	CartItemResponseDto addCart(Long memberId ,CartItemRequestDto reqDto);
	// 사용자 한명이 장바구니에 담은 모든 물건 조회
	List<CartItemResponseDto> getCartItem(Long memberId);
	// 장바구니에서 상품 한개 삭제(휴지통 버튼)
	void deleteCartItem(Long cartItemId);
	// 장바구니 상품 선택 삭제
	void deleteSeletedCartItems(List<Long> cartItemIds);
	// 장바구니 상품 전체 삭제
	void clearCart(Long memberId);
} 
 