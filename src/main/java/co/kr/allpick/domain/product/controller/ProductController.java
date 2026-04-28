package co.kr.allpick.domain.product.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.allpick.domain.product.controller.docs.ProductControllerDocs;
import co.kr.allpick.domain.product.dto.ProductDetailResponseDto;
import co.kr.allpick.domain.product.service.ProductService;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")

public class ProductController implements ProductControllerDocs{

	private final ProductService productService;

	@Override
	@GetMapping("/{productId}")
	public ResponseEntity<ApiResponse<ProductDetailResponseDto>> getProductDetail(@PathVariable("productId") Long productId) {
		return ApiResponse.success("상품을 조회합니다.", productService.getProductDetail(productId));

	}
}
