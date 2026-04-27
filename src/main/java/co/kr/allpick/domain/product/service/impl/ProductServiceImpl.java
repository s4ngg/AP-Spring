package co.kr.allpick.domain.product.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.product.dto.ProductDetailResDto;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.domain.product.service.ProductService;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
	
	private final ProductRepository productRepository;
	@Override
	@Transactional(readOnly = true)
	// Id로 상품상세 페이지 조회
 	public ProductDetailResDto getProductDetail(Long productId) {
		// findValidProduct 메서드가 판매상태와, 승인상태 검증해줌.
		Product product = productRepository.findValidProduct(productId)
				.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
		
		return ProductDetailResDto.from(product);
	}
}
  