package co.kr.allpick.domain.product.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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

import co.kr.allpick.domain.product.dto.ProductDetailResponseDto;
import co.kr.allpick.domain.product.dto.ProductSaveRequestDto;
import co.kr.allpick.domain.product.dto.ProductSaveResponseDto;
import co.kr.allpick.domain.product.entity.ParentCategory;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ParentCategoryRepository;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
<<<<<<< HEAD
=======
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
>>>>>>> 1532f82d6cdb563338ec42de8dbbcb835477de7d

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ParentCategoryRepository parentCategoryRepository; // 서비스 필드명과 일치

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @DisplayName("상품 상세 조회 성공 - 판매 중 및 승인된 상품")
    void 상품_상세_조회_성공() {
        // given
        Long productId = 1L;
        ParentCategory mockCategory = ParentCategory.builder().categoryName("전통주").build();
        
        Product mockProduct = Product.builder()
                .productId(productId)
                .parentCategory(mockCategory)
                .productName("느린마을 막걸리")
                .brand("배상면주가")
                .price(new BigDecimal("10000"))
                .optionList(new ArrayList<>())
                .productImageList(new ArrayList<>())
                .build();

        // 서비스에서 사용하는 findValidProduct 메서드 모킹
        when(productRepository.findValidProduct(productId)).thenReturn(Optional.of(mockProduct));

        // when
        ProductDetailResponseDto result = productService.getProductDetail(productId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProductName()).isEqualTo("느린마을 막걸리");
        assertThat(result.getBrand()).isEqualTo("배상면주가");
        verify(productRepository, times(1)).findValidProduct(productId);
    }

    @Test
    @DisplayName("상품 상세 조회 실패 - 존재하지 않거나 유효하지 않은 상품")
    void 상품_상세_조회_실패() {
        // given
        Long productId = 999L;
        when(productRepository.findValidProduct(productId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.getProductDetail(productId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
<<<<<<< HEAD
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
=======
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
>>>>>>> 1532f82d6cdb563338ec42de8dbbcb835477de7d
                .optionList(new ArrayList<>())
                .productImageList(new ArrayList<>())
                .build();

<<<<<<< HEAD
        Product mockProduct = reqDto.toEntity(mockCategory);

        // 레포지토리 동작 정의
        when(parentCategoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(productRepository.existsByProductName(reqDto.getProductName())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // when
        ProductSaveResponseDto result = productService.createProduct(reqDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).contains("성공");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("상품 등록 실패 - 중복된 상품명")
    void 상품_등록_실패_중복명() {
        // given
        ProductSaveRequestDto reqDto = ProductSaveRequestDto.builder()
                .categoryId(1L)
                .productName("이미있는상품")
                .build();

        when(parentCategoryRepository.findById(anyLong())).thenReturn(Optional.of(mock(ParentCategory.class)));
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
        when(parentCategoryRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.createProduct(reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CATEGORY_NOT_FOUND.getMessage());
    }
}
=======
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
>>>>>>> 1532f82d6cdb563338ec42de8dbbcb835477de7d
