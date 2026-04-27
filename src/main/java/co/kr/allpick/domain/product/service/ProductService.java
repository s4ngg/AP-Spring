package co.kr.allpick.domain.product.service;

import co.kr.allpick.domain.product.dto.ProductDetailResDto;

public interface ProductService {
	// 새로운 상품 생성하기
	void createProduct();
	// 상품 id로 상품상세 페이지 조회하기
	ProductDetailResDto getProductDetail(Long productId);
}
