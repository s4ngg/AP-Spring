package co.kr.allpick.domain.product.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

import co.kr.allpick.domain.product.dto.ProductSaveRequestDto;
import co.kr.allpick.domain.product.dto.ProductSaveResponseDto;
import co.kr.allpick.domain.product.entity.ParentCategory;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ParentCategoryRepository;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ParentCategoryRepository categoryRepository; // 카테고리 조회를 위해 추가

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @DisplayName("상품 등록 성공")
    void 상품_등록_성공() {
        // given
        Long categoryId = 1L;
        ParentCategory mockCategory = ParentCategory.builder()
                .parentCategoryId(categoryId)
                .categoryName("전통주")
                .build();

        ProductSaveRequestDto reqDto = ProductSaveRequestDto.builder()
                .categoryId(categoryId)
                .productName("새로운 막걸리")
                .brand("올픽양조장")
                .price(new BigDecimal("15000"))
                .optionList(new ArrayList<>())
                .productImageList(new ArrayList<>())
                .build();

        Product mockProduct = reqDto.toEntity(mockCategory);

        // 레포지토리 동작 정의
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(productRepository.existsByProductName(reqDto.getProductName())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // when
        ProductSaveResponseDto result = productService.createProduct(reqDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).contains("성공");
        verify(productRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("상품 등록 실패 - 중복된 상품명")
    void 상품_등록_실패_중복명() {
        // given
        ProductSaveRequestDto reqDto = ProductSaveRequestDto.builder()
                .categoryId(1L)
                .productName("이미있는상품")
                .build();

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mock(ParentCategory.class)));
        // 상품명이 이미 존재한다고 설정
        when(productRepository.existsByProductName("이미있는상품")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> productService.createProduct(reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PRODUCT_ALREADY_EXISTS.getMessage()); 
    }

    @Test
    @DisplayName("상품 등록 실패 - 존재하지 않는 카테고리")
    void 상품_등록_실패_카테고리없음() {
        // given
        ProductSaveRequestDto reqDto = ProductSaveRequestDto.builder()
                .categoryId(999L)
                .build();

        // 카테고리가 없다고 설정
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.createProduct(reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CATEGORY_NOT_FOUND.getMessage());
    }
}