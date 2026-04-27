	package co.kr.allpick.domain.product.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.kr.allpick.domain.product.dto.ProductDetailResDto;
import co.kr.allpick.domain.product.entity.ParentCategory;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @DisplayName("상품 상세 조회 성공")
    void 상품_상세_조회_성공() {
        // given
        Long productId = 1L;
        
        // 가짜 카테고리 생성
        ParentCategory mockCategory = ParentCategory.builder()
                .categoryName("전통주")
                .build();

        // 가짜 상품 생성
        Product mockProduct = Product.builder()
                .productId(productId)
                .productName("프리미엄 막걸리")
                .brand("올픽양조장")
                .price(new BigDecimal("25000"))
                .parentCategory(mockCategory)
                .thumbnailUrl("https://allpick.com")
                .optionList(new ArrayList<>()) // 빈 리스트 초기화
                .productImageList(new ArrayList<>()) // 빈 리스트 초기화
                .build();

        // 레포지토리 동작 정의 (findValidProduct 호출 시 mockProduct 반환)
        when(productRepository.findValidProduct(productId)).thenReturn(Optional.of(mockProduct));

        // when
        ProductDetailResDto result = productService.getProductDetail(productId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getProductName()).isEqualTo("프리미엄 막걸리");
        assertThat(result.getParentCategoryName()).isEqualTo("전통주");
    }

    @Test
    @DisplayName("상품 상세 조회 실패 - 존재하지 않거나 판매중지된 상품")
    void 상품_상세_조회_실패_상품없음() {
        // given
        Long productId = 999L;
        
        // 레포지토리가 빈 값을 반환하도록 설정
        when(productRepository.findValidProduct(productId)).thenReturn(Optional.empty());

        // when & then
        // 팀원분 형식에 맞춰 BusinessException과 ErrorCode로 검증
        assertThatThrownBy(() -> productService.getProductDetail(productId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }
}