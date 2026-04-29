   package co.kr.allpick.domain.product.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.product.dto.ProductDetailResponseDto;
import co.kr.allpick.domain.product.dto.ProductSaveRequestDto;
import co.kr.allpick.domain.product.dto.ProductSaveResponseDto;
import co.kr.allpick.domain.product.entity.ParentCategory;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ParentCategoryRepository;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.domain.product.service.ProductService;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
	
	private final ProductRepository productRepository;
	private final  ParentCategoryRepository parentCategoryRepository;
	
	
	
	    //public Product toEntity(ParentCategory parentCategory) <- 이게 상품 생성하는거
	
	@Transactional
	@Override
	// 상품 생성 메서드
		//		-	검증 : 1. 존재하는 카테고리인지 2. 이미 사용중인 상품명인지 -> 매개변수 : 요청Dto을 통해 검사
		// 		- 	반환 : 생성된 객체의 id, 생성성공 메세지..
	public ProductSaveResponseDto createProduct(ProductSaveRequestDto reqDto) {
		// 카테고리 검증
		ParentCategory parentCategory = parentCategoryRepository.findById(reqDto.getCategoryId())
				.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
		// 상품 이름 검증
		if (productRepository.existsByProductName(reqDto.getProductName())) {
			throw new BusinessException(ErrorCode.PRODUCT_ALREADY_EXISTS);
		}
		
		// 상품 생성 (위의 검증 모두 통과)
		Product product = reqDto.toEntity(parentCategory);
		// 만들어진 상품 저장
		productRepository.save(product);
		// 응답객체로 변환하여 반환
		return ProductSaveResponseDto.from(product);
	}
	
	@Transactional(readOnly = true)
	@Override
	
	// Id로 상품상세 페이지 조회
 	public ProductDetailResponseDto getProductDetail(Long productId) {
		// findValidProduct 메서드가 판매상태와, 승인상태 검증해줌.
		Product product = productRepository.findValidProduct(productId)
				.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
		
		return ProductDetailResponseDto.from(product);
	}




}
  