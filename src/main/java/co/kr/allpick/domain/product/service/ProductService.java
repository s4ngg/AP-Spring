package co.kr.allpick.domain.product.service;

import org.springframework.stereotype.Service;

import co.kr.allpick.domain.product.dto.ProductDetailResDto;


public interface ProductService {
	// 상품 id로 상품상세 페이지 조회하기
	ProductDetailResDto getProductDetail(Long productId);
}
 