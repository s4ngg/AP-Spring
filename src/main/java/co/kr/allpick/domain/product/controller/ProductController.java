package co.kr.allpick.domain.product.controller;

import co.kr.allpick.domain.product.dto.ProductListResponseDto;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.allpick.domain.product.controller.docs.ProductControllerDocs;
import co.kr.allpick.domain.product.dto.ProductDetailResponseDto;
import co.kr.allpick.domain.product.dto.ProductSaveRequestDto;
import co.kr.allpick.domain.product.dto.ProductSaveResponseDto;
import co.kr.allpick.domain.product.service.ProductService;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")

public class ProductController implements ProductControllerDocs{

	private final ProductService productService;

//	@Override
	@PostMapping
	public ResponseEntity<ApiResponse<ProductSaveResponseDto>> createProduct(
			@RequestBody ProductSaveRequestDto productSaveRequestDto) {
		return ApiResponse.success("상품을 생성했습니다", productService.createProduct(productSaveRequestDto));
	}
	
	@Override
	@GetMapping
	public ResponseEntity<ApiResponse<Page<ProductListResponseDto>>> getProductList(
			@ParameterObject @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return ApiResponse.success("상품 목록을 조회합니다.", productService.getProductList(pageable));
	}

	@Override
	@GetMapping("/{productId}")
	public ResponseEntity<ApiResponse<ProductDetailResponseDto>> getProductDetail(@PathVariable("productId") Long productId) {
		return ApiResponse.success("상품을 조회합니다.", productService.getProductDetail(productId));

	}
}
