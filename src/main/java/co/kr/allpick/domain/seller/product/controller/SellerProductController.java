package co.kr.allpick.domain.seller.product.controller;

import co.kr.allpick.domain.seller.product.dto.SellerProductRequestDto;
import co.kr.allpick.domain.seller.product.dto.SellerProductResponseDto;
import co.kr.allpick.domain.seller.product.entity.SellerProduct;
import co.kr.allpick.domain.seller.product.service.SellerProductService;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seller/products")
@RequiredArgsConstructor
public class SellerProductController {

    private final SellerProductService sellerProductService;

    // 상품 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createProduct(
            @RequestParam Long sellerId,
            @RequestBody @Valid SellerProductRequestDto dto) {
        sellerProductService.createProduct(sellerId, dto);
        return ApiResponse.success("상품 등록 성공");
    }

    // 상품 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<SellerProductResponseDto>>> getProducts(
            @RequestParam Long sellerId) {
    	return ApiResponse.success("상품 전체 조회 성공", sellerProductService.getProducts(sellerId));
    }

    // 상품 카테고리별 조회
    @GetMapping("/category")
    public ResponseEntity<ApiResponse<List<SellerProductResponseDto>>> getProductsByCategory(
            @RequestParam Long sellerId,
            @RequestParam SellerProduct.Category category) {
    	return ApiResponse.success("카테고리 조회 성공", sellerProductService.getProductsByCategory(sellerId, category));
    }
    

    // 상품 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SellerProductResponseDto>>> searchProducts(
            @RequestParam Long sellerId,
            @RequestParam String name) {
    	return ApiResponse.success("상품 검색 성공", sellerProductService.searchProducts(sellerId, name));
    }

    // 상품 단건 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<SellerProductResponseDto>> getProduct(
            @RequestParam Long sellerId,
            @PathVariable Long productId) {
    	return ApiResponse.success("상품 조회 성공", sellerProductService.getProduct(sellerId, productId));
    }

    // 상품 수정
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @RequestParam Long sellerId,
            @PathVariable Long productId,
            @RequestBody @Valid SellerProductRequestDto dto) {
        sellerProductService.updateProduct(sellerId, productId, dto);
        return ApiResponse.success("상품 수정 성공");
    }

    // 상품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @RequestParam Long sellerId,
            @PathVariable Long productId) {
        sellerProductService.deleteProduct(sellerId, productId);
        return ApiResponse.success("상품 삭제 성공");
    }

    // 상품 상태 변경
    @PatchMapping("/{productId}/status")
    public ResponseEntity<ApiResponse<Void>> changeProductStatus(
            @RequestParam Long sellerId,
            @PathVariable Long productId,
            @RequestParam SellerProduct.Status status) {
        sellerProductService.changeProductStatus(sellerId, productId, status);
        return ApiResponse.success("상품 상태 변경 성공");
    }
}