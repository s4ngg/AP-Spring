package co.kr.allpick.domain.product.service;

import co.kr.allpick.domain.product.dto.ProductDetailResponseDto;

public interface ProductService {
	
	// 상품 id로 상품상세 페이지 조회하기
	ProductDetailResponseDto getProductDetail(Long productId);
}
