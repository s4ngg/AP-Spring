package co.kr.allpick.domain.product.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import co.kr.allpick.domain.product.dto.ProductDetailResponseDto;
import co.kr.allpick.domain.product.dto.ProductListResponseDto;
import co.kr.allpick.domain.product.dto.ProductSaveRequestDto;
import co.kr.allpick.domain.product.dto.ProductSaveResponseDto;
import co.kr.allpick.domain.product.dto.ProductUpdateRequestDto;
import co.kr.allpick.domain.product.dto.ProductUpdateResponseDto;
import co.kr.allpick.domain.product.dto.SellerProductListResponseDto;
import co.kr.allpick.domain.product.entity.ChildCategory;
import co.kr.allpick.domain.product.entity.ParentCategory;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ChildCategoryRepository;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.domain.review.entity.Review;
import co.kr.allpick.domain.review.repository.ReviewRepository;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ChildCategoryRepository childCategoryRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    // --- 상품 상세 조회 테스트 ---

    @Test
    @DisplayName("상품 상세 조회 성공 - 판매 중 및 승인된 상품과 리뷰 페이징 확인")
    void 상품_상세_조회_성공() {
        Long productId = 1L;
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        ParentCategory mockParent = ParentCategory.builder().categoryName("전통주").build();
        ChildCategory mockCategory = ChildCategory.builder().categoryName("막걸리").parentCategory(mockParent).build();

        Product mockProduct = Product.builder()
                .productId(productId)
                .childCategory(mockCategory)
                .productName("느린마을 막걸리")
                .brand("배상면주가")
                .price(new BigDecimal("10000"))
                .optionList(new ArrayList<>())
                .productImageList(new ArrayList<>())
                .build();

        Page<Review> mockReviewPage = new PageImpl<>(List.of(), pageable, 0);

        when(productRepository.findValidProduct(productId)).thenReturn(Optional.of(mockProduct));
        when(reviewRepository.findByProductId(productId, pageable)).thenReturn(mockReviewPage);

        ProductDetailResponseDto result = productService.getProductDetail(productId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getProductName()).isEqualTo("느린마을 막걸리");
        assertThat(result.getParentCategoryName()).isEqualTo("전통주");
        assertThat(result.getReviewList()).isNotNull();

        verify(productRepository, times(1)).findValidProduct(productId);
        verify(reviewRepository, times(1)).findByProductId(productId, pageable);
    }

    @Test
    @DisplayName("상품 상세 조회 실패 - 존재하지 않거나 유효하지 않은 상품")
    void 상품_상세_조회_실패() {
        Long productId = 999L;
        Pageable pageable = PageRequest.of(0, 5);
        when(productRepository.findValidProduct(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductDetail(productId, pageable))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    // --- 상품 등록 테스트 ---

    @Test
    @DisplayName("상품 등록 성공")
    void 상품_등록_성공() {
        Long memberId = 1L; // ✅ 추가
        Long categoryId = 1L;

        Seller mockSeller = Seller.builder().build(); 

        ChildCategory mockCategory = ChildCategory.builder()
                .childCategoryId(categoryId)
                .categoryName("전통주")
                .build();

        ProductSaveRequestDto reqDto = ProductSaveRequestDto.builder()
                .categoryId(categoryId)
                .productName("새로운 막걸리")
                .brand("올픽양조장")
                .price(new BigDecimal("15000"))
                .build();

        Product mockProduct = Product.builder()
                .productName(reqDto.getProductName())
                .childCategory(mockCategory)
                .build();

        when(sellerRepository.findWithMemberByMemberId(memberId)).thenReturn(Optional.of(mockSeller));
        when(childCategoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(productRepository.existsByProductName(reqDto.getProductName())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        ProductSaveResponseDto result = productService.createProduct(memberId, reqDto); 

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).contains("성공");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("상품 등록 실패 - 판매자 아님") 
    void 상품_등록_실패_판매자아님() {
        Long memberId = 999L;
        ProductSaveRequestDto reqDto = ProductSaveRequestDto.builder()
                .categoryId(1L)
                .productName("새로운 막걸리")
                .build();

        when(sellerRepository.findWithMemberByMemberId(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(memberId, reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.NOT_SELLER.getMessage());
    }

    @Test
    @DisplayName("상품 등록 실패 - 중복된 상품명")
    void 상품_등록_실패_중복명() {
        Long memberId = 1L; 
        ProductSaveRequestDto reqDto = ProductSaveRequestDto.builder()
                .categoryId(1L)
                .productName("이미있는상품")
                .build();

        when(sellerRepository.findWithMemberByMemberId(memberId)).thenReturn(Optional.of(Seller.builder().build()));
        when(childCategoryRepository.findById(anyLong())).thenReturn(Optional.of(mock(ChildCategory.class)));
        when(productRepository.existsByProductName("이미있는상품")).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(memberId, reqDto)) // ✅ memberId 추가
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PRODUCT_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("상품 등록 실패 - 존재하지 않는 카테고리")
    void 상품_등록_실패_카테고리없음() {
        Long memberId = 1L; 
        ProductSaveRequestDto reqDto = ProductSaveRequestDto.builder()
                .categoryId(999L)
                .build();

        when(sellerRepository.findWithMemberByMemberId(memberId)).thenReturn(Optional.of(Seller.builder().build()));
        when(childCategoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(memberId, reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CATEGORY_NOT_FOUND.getMessage());
    }

    // --- 상품 목록 조회 테스트 ---

    @Test
    @DisplayName("상품 목록 조회 성공")
    void 상품_목록_조회_성공() {
        ParentCategory mockParent = ParentCategory.builder().categoryName("뷰티").build();
        ChildCategory mockCategory = ChildCategory.builder().categoryName("스킨케어").parentCategory(mockParent).build();

        Product mockProduct = Product.builder()
                .productId(1L)
                .productName("갈색병 세럼 50ml")
                .brand("에스티로더")
                .price(new BigDecimal("89000"))
                .childCategory(mockCategory)
                .thumbnailUrl("https://allpick.com")
                .optionList(new ArrayList<>())
                .productImageList(new ArrayList<>())
                .build();

        Pageable pageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> mockPage = new PageImpl<>(List.of(mockProduct), pageable, 1);

        given(productRepository.findVisibleProducts(
                Product.Status.ON_SALE, Product.ApprovalStatus.APPROVED, pageable))
                .willReturn(mockPage);

        Page<ProductListResponseDto> result = productService.getProductList(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getProductName()).isEqualTo("갈색병 세럼 50ml");
        assertThat(result.getContent().get(0).getParentCategoryName()).isEqualTo("뷰티");
    }

    @Test
    @DisplayName("상품 목록 조회 성공 - 조회된 상품 없음")
    void 상품_목록_조회_결과_없음() {
        Pageable pageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        given(productRepository.findVisibleProducts(
                Product.Status.ON_SALE, Product.ApprovalStatus.APPROVED, pageable))
                .willReturn(emptyPage);

        Page<ProductListResponseDto> result = productService.getProductList(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("판매자 상품 목록 조회 성공 - 승인 상태 포함")
    void 판매자_상품_목록_조회_성공_승인상태포함() {
        Long memberId = 1L;
        Long sellerId = 10L;
        Seller seller = Seller.builder()
                .sellerId(sellerId)
                .build();
        ParentCategory mockParent = ParentCategory.builder().categoryName("뷰티").build();
        ChildCategory category = ChildCategory.builder().categoryName("스킨케어").parentCategory(mockParent).build();
        Product pendingProduct = Product.builder()
                .productId(1L)
                .productName("승인 대기 상품")
                .brand("올픽")
                .price(new BigDecimal("10000"))
                .childCategory(category)
                .thumbnailUrl("https://allpick.com/pending.jpg")
                .approvalStatus(Product.ApprovalStatus.PENDING)
                .build();
        Product rejectedProduct = Product.builder()
                .productId(2L)
                .productName("거절 상품")
                .brand("올픽")
                .price(new BigDecimal("20000"))
                .childCategory(category)
                .thumbnailUrl("https://allpick.com/rejected.jpg")
                .approvalStatus(Product.ApprovalStatus.SUSPENDED)
                .build();

        given(sellerRepository.findWithMemberByMemberId(memberId)).willReturn(Optional.of(seller));
        given(productRepository.findBySellerIdAndDeletedAtIsNull(sellerId))
                .willReturn(List.of(pendingProduct, rejectedProduct));

        List<SellerProductListResponseDto> result = productService.getSellerProducts(memberId);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(SellerProductListResponseDto::getApprovalStatus)
                .containsExactly(Product.ApprovalStatus.PENDING, Product.ApprovalStatus.SUSPENDED);
    }

    @Test
    @DisplayName("상품 수정 성공 - 승인 대기 상태")
    void 상품_수정_성공_승인대기상태() {
        Long memberId = 1L;
        Long productId = 1L;
        Product product = Product.builder()
                .productId(productId)
                .productName("기존 상품")
                .brand("올픽")
                .price(new BigDecimal("10000"))
                .approvalStatus(Product.ApprovalStatus.PENDING)
                .build();
        ProductUpdateRequestDto request = ProductUpdateRequestDto.builder()
                .price(new BigDecimal("15000"))
                .build();

        given(productRepository.findByProductIdAndMemberId(memberId, productId))
                .willReturn(Optional.of(product));

        ProductUpdateResponseDto result = productService.updateProduct(memberId, productId, request);

        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("15000"));
    }

    @Test
    @DisplayName("상품 수정 실패 - 승인 완료 상태")
    void 상품_수정_실패_승인완료상태() {
        Long memberId = 1L;
        Long productId = 1L;
        Product product = Product.builder()
                .productId(productId)
                .price(new BigDecimal("10000"))
                .approvalStatus(Product.ApprovalStatus.APPROVED)
                .build();
        ProductUpdateRequestDto request = ProductUpdateRequestDto.builder()
                .price(new BigDecimal("15000"))
                .build();

        given(productRepository.findByProductIdAndMemberId(memberId, productId))
                .willReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateProduct(memberId, productId, request))
                .isInstanceOfSatisfying(BusinessException.class,
                        e -> assertThat(e.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_CANNOT_UPDATE_NOT_PENDING));
    }

    @Test
    @DisplayName("상품 수정 실패 - 정지 상태")
    void 상품_수정_실패_정지상태() {
        Long memberId = 1L;
        Long productId = 1L;
        Product product = Product.builder()
                .productId(productId)
                .price(new BigDecimal("10000"))
                .approvalStatus(Product.ApprovalStatus.SUSPENDED)
                .build();
        ProductUpdateRequestDto request = ProductUpdateRequestDto.builder()
                .price(new BigDecimal("15000"))
                .build();

        given(productRepository.findByProductIdAndMemberId(memberId, productId))
                .willReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateProduct(memberId, productId, request))
                .isInstanceOfSatisfying(BusinessException.class,
                        e -> assertThat(e.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_CANNOT_UPDATE_NOT_PENDING));
    }
}
