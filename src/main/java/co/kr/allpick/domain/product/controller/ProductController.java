package co.kr.allpick.domain.product.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import co.kr.allpick.domain.product.controller.docs.ProductControllerDocs;
import co.kr.allpick.domain.product.dto.ProductDetailResponseDto;
import co.kr.allpick.domain.product.dto.ProductListResponseDto;
import co.kr.allpick.domain.product.dto.ProductSaveRequestDto;
import co.kr.allpick.domain.product.dto.ProductSaveResponseDto;
import co.kr.allpick.domain.product.dto.ProductUpdateRequestDto;
import co.kr.allpick.domain.product.dto.ProductUpdateResponseDto;
import co.kr.allpick.domain.product.dto.SellerProductListResponseDto;
import co.kr.allpick.domain.product.service.ProductService;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestPart;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")

public class ProductController implements ProductControllerDocs{

	private final ProductService productService;

	@Override
	@PostMapping
	public ResponseEntity<ApiResponse<ProductSaveResponseDto>> createProduct(
			@AuthenticationPrincipal JwtUserInfoDto userInfo,
			@RequestBody ProductSaveRequestDto productSaveRequestDto) {
		return ApiResponse.success("상품을 생성했습니다", productService.createProduct(userInfo.getMemberId(), productSaveRequestDto));
	} 
	
	@Override
	@GetMapping
	public ResponseEntity<ApiResponse<Page<ProductListResponseDto>>> getProductList(
			@ParameterObject @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return ApiResponse.success("상품 목록을 조회합니다.", productService.getProductList(pageable));
	}
	@Override
	@GetMapping("/{productId}")
	public ResponseEntity<ApiResponse<ProductDetailResponseDto>> getProductDetail (
			@PathVariable("productId") Long productId,
			@PageableDefault(size = 5, sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable) {
		return ApiResponse.success("상품을 조회합니다.", productService.getProductDetail(productId, pageable));
 
	}
	@Override
	@PatchMapping("/{productId}")
	public ResponseEntity<ApiResponse<ProductUpdateResponseDto>> updateProduct (
			@AuthenticationPrincipal JwtUserInfoDto userInfo,
			@PathVariable("productId") Long productId,
			@RequestBody @Valid ProductUpdateRequestDto productUpdateRequestDto) {
		return ApiResponse.success("상품을 수정했습니다.", productService.updateProduct(userInfo.getMemberId(),productId, productUpdateRequestDto));
	}
	@PostMapping("/images")
	public ResponseEntity<ApiResponse<String>> uploadImage(
	    @RequestPart("image") MultipartFile image) throws IOException {
	    String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
	    Path path = Paths.get("uploads/" + fileName);
	    Files.createDirectories(path.getParent());
	    Files.write(path, image.getBytes());
	    String url = "http://localhost:8080/uploads/" + fileName;
	    return ApiResponse.success("이미지 업로드 성공", url);
	}
	@Override 
	@DeleteMapping("/{productId}")
	public ResponseEntity<ApiResponse<Void>> deleteProduct (
			@AuthenticationPrincipal JwtUserInfoDto userInfo,
			@PathVariable("productId") Long productId) {
		
		productService.deleteProduct(userInfo.getMemberId(), productId);
		return ApiResponse.success("상품을 삭제했습니다");
	}
	@Override
	@GetMapping("/seller")
	public ResponseEntity<ApiResponse<List<SellerProductListResponseDto>>> getSellerProducts(
	        @AuthenticationPrincipal JwtUserInfoDto userInfo) {
	    return ApiResponse.success("판매자 상품 목록 조회 성공",
	            productService.getSellerProducts(userInfo.getMemberId()));
	}
	

	
}
  
 





