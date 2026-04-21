package co.kr.allpick.domain.product.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.allpick.domain.product.dto.ProductReqDto;
import co.kr.allpick.domain.product.dto.ProductResDto;
import co.kr.allpick.domain.product.dto.ProductSearchReqDto;
import co.kr.allpick.domain.product.dto.ProductSearchResDto;
import co.kr.allpick.domain.product.service.ProductService;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
	
	private final ProductService productService;
	
	@PostMapping		// 상품 등록 처리 
	public ResponseEntity<ApiResponse<ProductResDto>> createProduct(
			@Valid @RequestBody ProductReqDto reqDto) {
		return ApiResponse.success("상품이 등록되었습니다.", productService.createProduct(reqDto));
	};
	
	@GetMapping("/{productId}")		// 단일 상품의 상세정보 조회
	public ResponseEntity<ApiResponse<ProductResDto>> getProductDetails(
			@PathVariable("productId") Long productId ) {
		return ApiResponse.success("상품의 정보입니다.", productService.getProductDetail(productId));
	}
	
	
	
	     
}
