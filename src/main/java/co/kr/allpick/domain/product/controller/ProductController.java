package co.kr.allpick.domain.product.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.allpick.domain.product.dto.ProductReqDto;
import co.kr.allpick.domain.product.dto.ProductResDto;
import co.kr.allpick.domain.product.service.ProductService;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
	
	private final ProductService productService;

	@PostMapping	// 상품 등록 : 요청온거 등록 후, 응답 반환
	
	ResponseEntity<ApiResponse<ProductResDto>> createProduct(
			
			@RequestBody @Valid ProductReqDto reqDto) {

		return ApiResponse.success("상품이 등록되었습니다.", productService.createProduct(reqDto));
	}  
	@GetMapping("/productAll")		// 상품 조회 (전체) : 전체 조회 후 응답 반환
	ResponseEntity<ApiResponse<List<ProductResDto>>> searchProducts() {
		return ApiResponse.success("전체상품을 조회합니다.", productService.searchProducts());
	} 
	@GetMapping("/{productId}") 
	ResponseEntity<ApiResponse<ProductResDto>> searchProduct(
			@PathVariable Long productId) {
		return ApiResponse.success("조회된 상품입니다.", productService.searchProduct(productId));
	}    
//	@DeleteMapping				// 상품 삭제 (전체)		
 //	ReponseEntity<ApiReponse<ProductResDto>> delectProducts() {		
//		return Api
//	}
//	
//	@DeleteMapping("/{productId}")
//	ReponseEntity<ApiResponse<ProductResDto>> 
}
