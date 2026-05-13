package co.kr.allpick.domain.cart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.allpick.domain.cart.controller.docs.CartItemControllerDocs;
import co.kr.allpick.domain.cart.dto.CartItemDeleteRequestDto;
import co.kr.allpick.domain.cart.dto.CartItemRequestDto;
import co.kr.allpick.domain.cart.dto.CartItemResponseDto;
import co.kr.allpick.domain.cart.service.CartService;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartItemController implements CartItemControllerDocs {
	private final CartService cartService;
	 
	// 사용자의 장바구니에 상품 추가
	@PostMapping
	public ResponseEntity<ApiResponse<CartItemResponseDto>> addCart(
			@AuthenticationPrincipal JwtUserInfoDto userInfo,
			@RequestBody @Valid CartItemRequestDto reqDto) {
		return ApiResponse.success("장바구니에 상품이 추가되었습니다.", cartService.addCart(userInfo.getMemberId(),reqDto));
	}
	// 사용자 한명의 장바구니에 담긴 상품 조회
	@GetMapping("/{memberId}")
	public ResponseEntity<ApiResponse<List<CartItemResponseDto>>> getCartItem(
			@AuthenticationPrincipal JwtUserInfoDto userInfo
			) { 
		return ApiResponse.success("장바구니 목록을 성공적으로 불러왔습니다.", cartService.getCartItem(userInfo.getMemberId()));
	}
	@DeleteMapping("/{cartItemId}")
	// 장바구니 상품 삭제 기능(휴지통 버튼)
	public ResponseEntity<ApiResponse<Void>> deleteCartItem(
			@PathVariable("cartItemId") Long cartItemId) {
		cartService.deleteCartItem(cartItemId); 
		return ApiResponse.success("제거했습니다.");
	}
	@DeleteMapping("/selected")
	public ResponseEntity<ApiResponse<Void>> deleteSelectedCartItems(
			@RequestBody @Valid CartItemDeleteRequestDto deleteDto ){
		cartService.deleteSeletedCartItems(deleteDto.getCartItemIds());
		return ApiResponse.success("선택한 상품을 제거했습니다.");
	} 
	@DeleteMapping("/clear")
	public ResponseEntity<ApiResponse<Void>> clearCart(
			@AuthenticationPrincipal JwtUserInfoDto userInfo) {
		cartService.clearCart(userInfo.getMemberId());
		return ApiResponse.success("모든 상품을 제거했습니다.");
	}
	 
	
	
	
}
 