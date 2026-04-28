package co.kr.allpick.domain.seller.product.service;

import co.kr.allpick.domain.seller.product.dto.SellerProductRequestDto;
import co.kr.allpick.domain.seller.product.dto.SellerProductResponseDto;
import co.kr.allpick.domain.seller.product.entity.SellerProduct;
import java.util.List;

public interface SellerProductService {

    // 상품 등록
    void createProduct(Long sellerId, SellerProductRequestDto dto);

    // 상품 전체 조회
    List<SellerProductResponseDto> getProducts(Long sellerId);

    // 상품 카테고리별 조회
    List<SellerProductResponseDto> getProductsByCategory(Long sellerId, SellerProduct.Category category);

    // 상품 검색
    List<SellerProductResponseDto> searchProducts(Long sellerId, String name);

    // 상품 단건 조회
    SellerProductResponseDto getProduct(Long sellerId, Long productId);

    // 상품 수정
    void updateProduct(Long sellerId, Long productId, SellerProductRequestDto dto);

    // 상품 삭제
    void deleteProduct(Long sellerId, Long productId);

    // 상품 상태 변경
    void changeProductStatus(Long sellerId, Long productId, SellerProduct.Status status);
}