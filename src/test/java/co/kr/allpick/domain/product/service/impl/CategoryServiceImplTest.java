//package co.kr.allpick.domain.product.service.impl;
//
//import co.kr.allpick.domain.product.dto.CategoryProductResponseDto;
//import co.kr.allpick.domain.product.entity.Product;
//import co.kr.allpick.domain.product.repository.ProductRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class CategoryServiceImplTest {
//
//    @Mock
//    ProductRepository productRepository;
//
//    @InjectMocks
//    CategoryServiceImpl categoryService;
//
//    @Test
//    @DisplayName("카테고리별 상품 목록 조회 성공")
//    void 카테고리별_상품_목록_조회_성공() {
//        // given
//        Long categoryId = 1L;
//
//        Product product1 = new Product(
//                1L, 1L, "상품1",
//                "설명1", 10000, "image1.jpg",
//                "ACTIVE", "APPROVED"
//        );
//
//        Product product2 = new Product(
//                1L, 2L, "상품2",
//                "설명2", 20000, "image2.jpg",
//                "ACTIVE", "APPROVED"
//        );
//
//        when(productRepository.findByCategoryId(categoryId))
//                .thenReturn(List.of(product1, product2));
//
//        // when
//        List<CategoryProductResponseDto> result = categoryService.getProductsByCategory(categoryId);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result).hasSize(2);
//
//        assertThat(result.get(0).getProductName()).isEqualTo("상품1");
//        assertThat(result.get(0).getPrice()).isEqualTo(10000);
//        assertThat(result.get(0).getThumbnailUrl()).isEqualTo("image1.jpg");
//
//        assertThat(result.get(1).getProductName()).isEqualTo("상품2");
//        assertThat(result.get(1).getPrice()).isEqualTo(20000);
//        assertThat(result.get(1).getThumbnailUrl()).isEqualTo("image2.jpg");
//    }
//
//    @Test
//    @DisplayName("카테고리별 상품 목록 조회 실패 - 조회된 상품 없음")
//    void 카테고리별_상품_목록_조회_실패_조회된_상품_없음() {
//        // given
//        Long categoryId = 99L;
//
//        when(productRepository.findByCategoryId(categoryId))
//                .thenReturn(List.of());
//
//        // when
//        List<CategoryProductResponseDto> result = categoryService.getProductsByCategory(categoryId);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result).isEmpty();
//    }
//}