package co.kr.allpick.domain.cart.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.cart.dto.CartItemRequestDto;
import co.kr.allpick.domain.cart.dto.CartItemResponseDto;
import co.kr.allpick.domain.cart.entity.Cart;
import co.kr.allpick.domain.cart.entity.CartItem;
import co.kr.allpick.domain.cart.repository.CartItemRepository;
import co.kr.allpick.domain.cart.repository.CartRepository;
import co.kr.allpick.domain.cart.service.CartItemService;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService{
	
	private final CartRepository cartRepository;
	private final ProductRepository productRepository;
	private final CartItemRepository cartItemRepository;
	private final MemberRepository memberRepository;
	
	@Transactional
	@Override
	// 장바구니에 상품 추가
	// reqdto: 상품, 장바구니, 수량 지정 가능
	// 상품, 장바구니를 레포지토리에서 조회해서, cartItem 엔티티에 행으로 저장하기.
	// ! 만약 장바구니가 없다면 새로 만들어줌.
	
	public CartItemResponseDto addCart(CartItemRequestDto reqDto, Long memberId) {
		
		// 어떤 사용자의 장바구니? -> 장바구니가 없다면 새로운 장바구니 생성 
		Cart cart = cartRepository.findByMemberId(memberId)
				.orElseGet(() -> {
					
					// (신규회원인지? 저장된 회원이 아닌건지 검증)
					Member member = memberRepository.findById(memberId)
							.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
					// 신규회원이면 장바구니 생성해줌
					Cart newCart = Cart.createCart(member);
					
					return cartRepository.save(newCart); 
				});
		// 어떤 상품인지?
		Product product = productRepository.findById(reqDto.getProductId())
				.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
		
		// 모두 특정한 후에, 장바구니 상품 테이블에 행으로 저장. (위에서  사용자,장바구니,상품,수량까지 특정해둔 상태 )
		CartItem cartItem = CartItemRequestDto.addToCart(product, cart, reqDto.getQuantity());
		cartItemRepository.save(cartItem);
		
		// 장바구니 상품 테이블에 행으로 저장된거 응답객체로 바꿔서 반환
		return CartItemResponseDto.from(cartItem);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<CartItemResponseDto> getCartItem(Long memberId) {
		// 해당 사용자의 장바구니를 조회함.
		List<CartItem> result = cartItemRepository.findByCartList(memberId);
		
		return result.stream()
				.map(CartItemResponseDto::from)
				.toList();
				
			
	}
}








   