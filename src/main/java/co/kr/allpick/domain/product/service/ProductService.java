package co.kr.allpick.domain.product.service;


import co.kr.allpick.domain.product.dto.ProductDetailResponseDto;
import co.kr.allpick.domain.product.dto.ProductSaveRequestDto;
import co.kr.allpick.domain.product.dto.ProductSaveResponseDto;

import co.kr.allpick.domain.product.dto.ProductListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProductService {
	// 상품 생성 메서드
	//		-	검증 : 1. 존재하는 카테고리인지 2. 이미 사용중인 상품명인지 -> 매개변수 : 요청Dto을 통해 검사
	// 		- 	반환 : 생성된 객체의 id, 생성성공 메세지.. 
	ProductSaveResponseDto createProduct(ProductSaveRequestDto reqDto);
	 
	// 상품 id로 상품상세 페이지 조회하기

	ProductDetailResponseDto getProductDetail(Long productId);


	// 상품 목록 페이지 조회
	Page<ProductListResponseDto> getProductList(Pageable pageable);

}
