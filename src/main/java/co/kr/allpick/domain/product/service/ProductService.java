package co.kr.allpick.domain.product.service;

import co.kr.allpick.domain.product.dto.ProductDetailResDto;
import co.kr.allpick.domain.product.dto.ProductListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
	// 상품 id로 상품상세 페이지 조회하기
	ProductDetailResDto getProductDetail(Long productId);

	// 상품 목록 페이지 조회
	Page<ProductListResponseDto> getProductList(Pageable pageable);
}
