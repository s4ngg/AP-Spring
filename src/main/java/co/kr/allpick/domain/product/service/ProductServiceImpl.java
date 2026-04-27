package co.kr.allpick.domain.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.product.dto.ProductDetailResDto;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	

	@Transactional(readOnly = true)
	@Override // 상품 단건 조회 (상품 클릭 했을 때)
	public ProductDetailResDto getProductDetail(Long id) {
		Product result = productRepository.findById(id)
				.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
		return ProductDetailResDto.from(result);
	}

}
 