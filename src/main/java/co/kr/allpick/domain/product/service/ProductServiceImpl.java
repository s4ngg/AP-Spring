package co.kr.allpick.domain.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.product.dto.ProductReqDto;
import co.kr.allpick.domain.product.dto.ProductResDto;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	@Transactional
	@Override // 상품 등록 : 요청온거 등록 후, 응답 반환
	public ProductResDto createProduct(ProductReqDto reqDto) {
		// 이미 존재하는 상품인지 먼저 검증
		if (productRepository.existsByProductName(reqDto.getProductName())) {
			throw new BusinessException(ErrorCode.PRODUCT_ALREADY_EXISTS);
		}
		// 요청 들어온거 엔티티 객체로 만들기
		Product product = Product.ToEntity(reqDto);
		// 만든 엔티티 객체를 실제로 저장
		Product savedProduct = productRepository.save(product);
		// 엔티티 객체를 응답객체로 변환 후, 반환
		return ProductResDto.fromEntity(savedProduct);
	}

	@Transactional(readOnly = true)
	@Override // 상품 단건 조회 (상품 클릭 했을 때)
	public ProductResDto getProductDetail(Long id) {
		Product result = productRepository.findById(id)
				.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
		return ProductResDto.fromEntity(result);
	}

}
