package co.kr.allpick.domain.cart.service;

import java.util.List;

import org.springframework.stereotype.Service;

import co.kr.allpick.domain.cart.dto.ProductReqDto;
import co.kr.allpick.domain.cart.dto.ProductResDto;


public interface ProductService {
	// 처리 후 -> 응답해주기
	
	// 상품 등록	: 요청온거 등록 후, 응답 반환
	ProductResDto createProduct(ProductReqDto reqDto);
	// 상품 조회 (전체)		: 	전체 조회 후 응답 반환	
	List<ProductResDto> searchProducts();
	// 상품 조회 (단건)
	ProductResDto searchProduct(Long productId);
	// 상품 삭제 (전체)
	void deleteProducts();
	// 상품 삭제 (단건)
	void deleteProduct(Long productId);
	
	// 상품 수정 (... 은 나중에 추가)
}
