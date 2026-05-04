   package co.kr.allpick.domain.product.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.product.dto.ProductDetailResponseDto;
import co.kr.allpick.domain.product.dto.ProductListResponseDto;
import co.kr.allpick.domain.product.dto.ProductSaveRequestDto;
import co.kr.allpick.domain.product.dto.ProductSaveResponseDto;
import co.kr.allpick.domain.product.dto.ProductUpdateRequestDto;
import co.kr.allpick.domain.product.dto.ProductUpdateResponseDto;
import co.kr.allpick.domain.product.entity.ParentCategory;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ParentCategoryRepository;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.domain.product.service.ProductService;
import co.kr.allpick.domain.review.dto.ReviewResponseDto;
import co.kr.allpick.domain.review.repository.ReviewRepository;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

	private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

	private final ProductRepository productRepository;
	private final  ParentCategoryRepository parentCategoryRepository;
	private final ReviewRepository reviewRepository;
	private final SellerRepository sellerRepository;
	
	@Override
	// 상품 생성 메서드
		//		-	검증 : 1.해당 상품의 판매자인지 2. 존재하는 카테고리인지 3. 이미 사용중인 상품명인지 -> 매개변수 : 요청Dto을 통해 검사
		// 		- 	반환 : 생성된 객체의 id, 생성성공 메세지..
	public ProductSaveResponseDto createProduct(Long memberId ,ProductSaveRequestDto reqDto) {
		
		// 판매자 검증 	
		Seller seller = sellerRepository.findWithMemberByMemberId(memberId)
				.orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLER));
		// 카테고리 검증
		ParentCategory parentCategory = parentCategoryRepository.findById(reqDto.getCategoryId())
				.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
		// 상품 이름 검증
		if (productRepository.existsByProductName(reqDto.getProductName())) {
			throw new BusinessException(ErrorCode.PRODUCT_ALREADY_EXISTS);
		}
		
		// 상품 생성 (위의 검증 모두 통과)
		Product product = reqDto.toEntity(seller, parentCategory);
		// 만들어진 상품 저장
		productRepository.save(product);
		// 응답객체로 변환하여 반환
		return ProductSaveResponseDto.from(product);
	}
	
	@Transactional(readOnly = true)
	@Override
	
	// Id로 상품상세 페이지 조회 (리뷰 포함)
 	public ProductDetailResponseDto getProductDetail(Long productId, Pageable pageable) {
		// findValidProduct 메서드가 판매상태와, 승인상태 검증해줌.
		Product product = productRepository.findValidProduct(productId)
				.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

		// 해당 상품에 대한 리뷰 조회
		Page<ReviewResponseDto> reviewPage = reviewRepository.findByProductId(productId,pageable)
				.map(ReviewResponseDto::from);
		
		return ProductDetailResponseDto.from(product, reviewPage);
	}
	
	@Override
	
	// 상품 가격 수정 ( PatchMapping )
	public ProductUpdateResponseDto updateProduct(Long memberId, Long productId, ProductUpdateRequestDto reqDto) {
		// 상품 존재 검증 + 판매자의 상품인지 검증 
		Product product = productRepository.findByProductIdAndMemberId(memberId, productId)
				.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
		product.updatePrice(reqDto.getPrice());
		return ProductUpdateResponseDto.from(product);
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
	
	@Override
	public void deleteProduct(Long memberId,Long productId) {
		// 해당 상품의 판매자인지 확인 
		Product product = productRepository.findByProductIdAndMemberId(memberId, productId)
				.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
		
		product.delete();
	}
	


}
