	package co.kr.allpick.domain.product.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import co.kr.allpick.domain.product.dto.ProductListResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
    //
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

    @Test
    @DisplayName("상품 목록 조회 성공")
    void 상품_목록_조회_성공() {
        // given
        ParentCategory mockCategory = ParentCategory.builder()
                .categoryName("뷰티")
                .build();

        Product mockProduct = Product.builder()
                .productId(1L)
                .productName("갈색병 세럼 50ml")
                .brand("에스티로더")
                .price(new BigDecimal("89000"))
                .parentCategory(mockCategory)
                .thumbnailUrl("https://allpick.com")
                .optionList(new ArrayList<>())
                .productImageList(new ArrayList<>())
                .build();

        Pageable pageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> mockPage = new PageImpl<>(List.of(mockProduct), pageable, 1);

        when(productRepository.findByStatusAndApprovalStatusAndDeletedAtIsNull(
                Product.Status.ON_SALE, Product.ApprovalStatus.APPROVED, pageable))
                .thenReturn(mockPage);

        // when
        Page<ProductListResponseDto> result = productService.getProductList(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getProductName()).isEqualTo("갈색병 세럼 50ml");
        assertThat(result.getContent().get(0).getParentCategoryName()).isEqualTo("뷰티");
    }

    @Test
    @DisplayName("상품 목록 조회 성공 - 조회된 상품 없음")
    void 상품_목록_조회_결과_없음() {
        // given
        Pageable pageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(productRepository.findByStatusAndApprovalStatusAndDeletedAtIsNull(
                Product.Status.ON_SALE, Product.ApprovalStatus.APPROVED, pageable))
                .thenReturn(emptyPage);

        // when
        Page<ProductListResponseDto> result = productService.getProductList(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
    }

}