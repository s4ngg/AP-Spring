package co.kr.allpick.domain.cart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.allpick.domain.cart.dto.CartItemRequestDto;
import co.kr.allpick.domain.cart.dto.CartItemResponseDto;
import co.kr.allpick.domain.cart.service.CartItemService;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartItemController {
	private final CartItemService cartItemService;
	
	// 사용자의 장바구니에 상품 추가
	@PostMapping("/{memberId}")
	public ResponseEntity<ApiResponse<CartItemResponseDto>> addCart(
			@PathVariable("memberId") Long memberId,
			@RequestBody @Valid CartItemRequestDto reqDto) {
		return ApiResponse.success("장바구니에 상품이 추가되었습니다.", cartItemService.addCart(reqDto, memberId));
	}
	// 사용자 한명의 장바구니에 담긴 상품 조회
	@GetMapping("/{memberId}")
	public ResponseEntity<ApiResponse<List<CartItemResponseDto>>> getCartItem(
			@PathVariable("memberId") Long memberId
			) {
		return ApiResponse.success("장바구니 목록을 성공적으로 불러왔습니다.", cartItemService.getCartItem(memberId));
	}
}
