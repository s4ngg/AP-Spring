package co.kr.allpick.domain.cart.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.kr.allpick.domain.cart.dto.CartItemRequestDto;
import co.kr.allpick.domain.cart.dto.CartItemResponseDto;
import co.kr.allpick.domain.cart.entity.Cart;
import co.kr.allpick.domain.cart.entity.CartItem;
import co.kr.allpick.domain.cart.repository.CartItemRepository;
import co.kr.allpick.domain.cart.repository.CartRepository;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.entity.ProductOption;
import co.kr.allpick.domain.product.repository.ProductOptionRepository;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    @Mock CartItemRepository cartItemRepository;
    @Mock CartRepository cartRepository;
    @Mock MemberRepository memberRepository;
    @Mock ProductRepository productRepository;
    @Mock ProductOptionRepository productOptionRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    // 공통 mock 객체 생성 헬퍼
    private Product buildMockProduct() {
        return Product.builder()
                .brand("나이키")
                .productName("에어포스")
                .price(BigDecimal.valueOf(17000))
                .build();
    }

    private ProductOption buildMockOption() {
        return ProductOption.builder()
                .optionValue("270")
                .build();
    }

    private CartItem buildMockCartItem(Product product, ProductOption option) {
        return CartItem.builder()
                .cartItemId(1L)
                .product(product)
                .productOption(option)
                .quantity(2)
                .build();
    }

    @Test
    @DisplayName("장바구니 목록 조회 성공")
    void 장바구니_목록_조회_성공() {
        Long memberId = 1L;

        CartItem mockCartItem = buildMockCartItem(buildMockProduct(), buildMockOption());

        when(cartItemRepository.findAllActiveByMemberId(memberId)).thenReturn(List.of(mockCartItem)); // ✅ 메서드명 수정

        List<CartItemResponseDto> result = cartService.getCartItem(memberId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBrandName()).isEqualTo("나이키");
        assertThat(result.get(0).getOption()).isEqualTo("270");
        assertThat(result.get(0).getPrice()).isEqualTo(17000);
    }

    @Test
    @DisplayName("장바구니 상품 추가 성공 - 기존 장바구니 있음")
    void 장바구니_상품_추가_성공() {
        Long memberId = 1L;
        CartItemRequestDto reqDto = CartItemRequestDto.builder()
                .productId(1L)
                .productOptionId(1L)
                .quantity(2)
                .build();

        Member mockMember = Member.builder().build();
        Cart mockCart = Cart.builder().build();
        Product mockProduct = buildMockProduct();
        ProductOption mockOption = buildMockOption();
        CartItem mockCartItem = buildMockCartItem(mockProduct, mockOption);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(mockCart));
        when(productRepository.findById(reqDto.getProductId())).thenReturn(Optional.of(mockProduct));
        when(productOptionRepository.findById(reqDto.getProductOptionId())).thenReturn(Optional.of(mockOption));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(mockCartItem);

        CartItemResponseDto result = cartService.addCart(memberId, reqDto);

        assertThat(result).isNotNull();
        assertThat(result.getBrandName()).isEqualTo("나이키");
        assertThat(result.getOption()).isEqualTo("270");
    }

    @Test
    @DisplayName("장바구니 상품 추가 실패 - 회원 없음")
    void 장바구니_상품_추가_실패_회원없음() {
        Long memberId = 999L;
        CartItemRequestDto reqDto = CartItemRequestDto.builder()
                .productId(1L)
                .productOptionId(1L)
                .quantity(1)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addCart(memberId, reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("장바구니 상품 추가 실패 - 상품 없음")
    void 장바구니_상품_추가_실패_상품없음() {
        Long memberId = 1L;
        CartItemRequestDto reqDto = CartItemRequestDto.builder()
                .productId(999L)
                .productOptionId(1L)
                .quantity(1)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(Member.builder().build()));
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(Cart.builder().build()));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addCart(memberId, reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("장바구니 단건 삭제 성공")
    void 장바구니_단건_삭제_성공() {
        Long cartItemId = 1L;
        CartItem mockCartItem = buildMockCartItem(buildMockProduct(), buildMockOption());

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(mockCartItem));

        cartService.deleteCartItem(cartItemId);

        verify(cartItemRepository).findById(cartItemId);
    }

    @Test
    @DisplayName("장바구니 단건 삭제 실패 - 상품 없음")
    void 장바구니_단건_삭제_실패() {
        Long cartItemId = 999L;

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.deleteCartItem(cartItemId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CART_ITEM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("장바구니 선택 삭제 성공")
    void 장바구니_선택_삭제_성공() {
        List<Long> cartItemIds = List.of(1L, 2L);
        CartItem mockCartItem1 = buildMockCartItem(buildMockProduct(), buildMockOption());
        CartItem mockCartItem2 = buildMockCartItem(buildMockProduct(), buildMockOption());

        when(cartItemRepository.findAllById(cartItemIds)).thenReturn(List.of(mockCartItem1, mockCartItem2));

        cartService.deleteSeletedCartItems(cartItemIds);

        verify(cartItemRepository).findAllById(cartItemIds);
    }

    @Test
    @DisplayName("장바구니 전체 비우기 성공")
    void 장바구니_전체_비우기_성공() {
        Long memberId = 1L;
        CartItem mockCartItem = buildMockCartItem(buildMockProduct(), buildMockOption());

        when(cartItemRepository.findAllActiveByMemberId(memberId)).thenReturn(List.of(mockCartItem));

        cartService.clearCart(memberId);

        verify(cartItemRepository).findAllActiveByMemberId(memberId);
    }
}