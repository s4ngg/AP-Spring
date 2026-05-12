package co.kr.allpick.domain.product.service;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import co.kr.allpick.domain.product.dto.ProductDetailResponseDto;
import co.kr.allpick.domain.product.dto.ProductListResponseDto;
import co.kr.allpick.domain.product.dto.ProductSaveRequestDto;
import co.kr.allpick.domain.product.dto.ProductSaveResponseDto;
import co.kr.allpick.domain.product.dto.ProductUpdateRequestDto;
import co.kr.allpick.domain.product.dto.ProductUpdateResponseDto;
import co.kr.allpick.domain.product.dto.SellerProductListResponseDto;


public interface ProductService {
	// 상품 생성 메서드
	ProductSaveResponseDto createProduct(Long memberId ,ProductSaveRequestDto reqDto);
	 
	// 상품 id로 상품상세 페이지 조회하기

	ProductDetailResponseDto getProductDetail(Long productId, Pageable pageable);

	// 상품 가격수정 메서드 ( PatchMapping ) 
	ProductUpdateResponseDto updateProduct(Long memberId ,Long productId, ProductUpdateRequestDto reqdto);

	// 상품 목록 페이지 조회
	Page<ProductListResponseDto> getProductList(Pageable pageable);
	
	// 상품 삭제 ( DeleteMapping )
	void deleteProduct(Long memberId ,Long productId);
	
	List<SellerProductListResponseDto> getSellerProducts(Long memberId);
}
