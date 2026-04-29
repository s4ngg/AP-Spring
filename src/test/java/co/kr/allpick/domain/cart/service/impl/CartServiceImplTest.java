package co.kr.allpick.domain.cart.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.kr.allpick.domain.cart.dto.CartItemResponseDto;
import co.kr.allpick.domain.cart.entity.CartItem;
import co.kr.allpick.domain.cart.repository.CartItemRepository;
import co.kr.allpick.domain.cart.service.CartService;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.entity.ProductOption;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {
	@Mock
	CartItemRepository cartItemRepository;	
	@InjectMocks
	private CartServiceImpl cartService;
	
	@Test	
	@DisplayName("장바구니 목록 조회 성공")
	void 장바구니_목록_조회_성공() {
		// given
		Long memberId = 1L;
		
		// 1. 상품(Product) 모킹
		Product mockProduct = Product.builder()
				.brand("나이키")
				.productName("에어포스")
				.price(BigDecimal.valueOf(17000)) 
				.build();
		
		// 2. 상품 옵션(ProductOption) 모킹 - Dto의 option 필드에 들어감
		ProductOption mockOption = ProductOption.builder()
				.optionValue("270")
				.build();
		
		// 3. 장바구니 아이템(CartItem) 생성 및 관계 연결
		CartItem mockCartItem = CartItem.builder()
				.cartItemId(1L)
				.product(mockProduct)
				.productOption(mockOption) // 이 부분이 있어야 NullPointerException이 안 납니다.
				.quantity(2)
				.build();
		
		when(cartItemRepository.findByCartList(memberId)).thenReturn(List.of(mockCartItem));
		
		// when
		List<CartItemResponseDto> result = cartService.getCartItem(memberId);
		
		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getBrandName()).isEqualTo("나이키");
		assertThat(result.get(0).getOption()).isEqualTo("270"); // 옵션 검증
		assertThat(result.get(0).getPrice()).isEqualTo(17000); // intValue() 변환 확인
	}
}

