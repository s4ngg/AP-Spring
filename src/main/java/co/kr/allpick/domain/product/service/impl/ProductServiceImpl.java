package co.kr.allpick.domain.product.service.impl;

import co.kr.allpick.domain.product.dto.ProductListResponseDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

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

	@Override
	@Transactional(readOnly = true)
	// 판매 중이고 승인된 상품 목록 페이지 조회
	public Page<ProductListResponseDto> getProductList(Pageable pageable) {
		logger.info("상품 목록 조회 - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
		return productRepository.findByStatusAndApprovalStatusAndDeletedAtIsNull(
				Product.Status.ON_SALE,   // 현재 판매중
				Product.ApprovalStatus.APPROVED,   // 관리자 승인 상품
				pageable
		).map(ProductListResponseDto::from);
	}

}
