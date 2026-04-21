package co.kr.allpick.domain.product.service;

import co.kr.allpick.domain.product.dto.ProductReqDto;
import co.kr.allpick.domain.product.dto.ProductResDto;

public interface ProductService {
	// 처리 후 -> 응답해주기
	
	// 상품 등록	: 요청온거 등록 후, 응답 반환
	ProductResDto createProduct(ProductReqDto reqDto);
	
	// 상품 단건 조회 (상품 클릭 했을 때)	
	ProductResDto getProductDetail(Long id);
}
