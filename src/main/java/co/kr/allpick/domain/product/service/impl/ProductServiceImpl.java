   package co.kr.allpick.domain.product.service.impl;

import java.util.List;

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
	// ?í’ˆ ?ì„± ë©”ì„œ??
		//		-	ê²€ì¦?: 1.?´ë‹¹ ?í’ˆ???ë§¤?ì¸ì§€ 2. ì¡´ì¬?˜ëŠ” ì¹´í…Œê³ ë¦¬?¸ì? 3. ?´ë? ?¬ìš©ì¤‘ì¸ ?í’ˆëª…ì¸ì§€ -> ë§¤ê°œë³€??: ?”ì²­Dto???µí•´ ê²€??
		// 		- 	ë°˜í™˜ : ?ì„±??ê°ì²´??id, ?ì„±?±ê³µ ë©”ì„¸ì§€..
	public ProductSaveResponseDto createProduct(Long memberId ,ProductSaveRequestDto reqDto) {
		
		// ?ë§¤??ê²€ì¦?	
		Seller seller = sellerRepository.findWithMemberByMemberId(memberId)
				.orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLER));
		// ì¹´í…Œê³ ë¦¬ ê²€ì¦?
		ParentCategory parentCategory = parentCategoryRepository.findById(reqDto.getCategoryId())
				.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
		// ?í’ˆ ?´ë¦„ ê²€ì¦?(?? œ???í’ˆ ?œì™¸)
		if (productRepository.existsByProductNameAndDeletedAtIsNull(reqDto.getProductName())) {
			throw new BusinessException(ErrorCode.PRODUCT_ALREADY_EXISTS);
		}
		
		// ?í’ˆ ?ì„± (?„ì˜ ê²€ì¦?ëª¨ë‘ ?µê³¼)
		Product product = reqDto.toEntity(seller, parentCategory);
		// ë§Œë“¤?´ì§„ ?í’ˆ ?€??
		productRepository.save(product);
		// ?‘ë‹µê°ì²´ë¡?ë³€?˜í•˜??ë°˜í™˜
		return ProductSaveResponseDto.from(product);
	}
	
	@Transactional(readOnly = true)
	@Override
	
	// Idë¡??í’ˆ?ì„¸ ?˜ì´ì§€ ì¡°íšŒ (ë¦¬ë·° ?¬í•¨)
 	public ProductDetailResponseDto getProductDetail(Long productId, Pageable pageable) {
		// findValidProduct ë©”ì„œ?œê? ?ë§¤?íƒœ?€, ?¹ì¸?íƒœ ê²€ì¦í•´ì¤?
		Product product = productRepository.findValidProduct(productId)
				.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

		// ?´ë‹¹ ?í’ˆ???€??ë¦¬ë·° ì¡°íšŒ
		Page<ReviewResponseDto> reviewPage = reviewRepository.findByProductId(productId,pageable)
				.map(ReviewResponseDto::from);
		
		return ProductDetailResponseDto.from(product, reviewPage);
	}
	
	@Override
	
	// ?í’ˆ ê°€ê²??˜ì • ( PatchMapping )
	public ProductUpdateResponseDto updateProduct(Long memberId, Long productId, ProductUpdateRequestDto reqDto) {
		// ?í’ˆ ì¡´ì¬ ê²€ì¦?+ ?ë§¤?ì˜ ?í’ˆ?¸ì? ê²€ì¦?
		Product product = productRepository.findByProductIdAndMemberId(memberId, productId)
				.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
		product.updatePrice(reqDto.getPrice());
		return ProductUpdateResponseDto.from(product);
	}

	@Override
	@Transactional(readOnly = true)
	// ?ë§¤ ì¤‘ì´ê³??¹ì¸???í’ˆ ëª©ë¡ ?˜ì´ì§€ ì¡°íšŒ
	public Page<ProductListResponseDto> getProductList(Pageable pageable) {
		logger.info("?í’ˆ ëª©ë¡ ì¡°íšŒ - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
		return productRepository.findByStatusAndApprovalStatusAndDeletedAtIsNull(
				Product.Status.ON_SALE,   // ?„ì¬ ?ë§¤ì¤?
				Product.ApprovalStatus.APPROVED,   // ê´€ë¦¬ì ?¹ì¸ ?í’ˆ
				pageable
		).map(ProductListResponseDto::from);
	}
	
	@Override
	public void deleteProduct(Long memberId,Long productId) {
		// ?´ë‹¹ ?í’ˆ???ë§¤?ì¸ì§€ ?•ì¸ 
		Product product = productRepository.findByProductIdAndMemberId(memberId, productId)
				.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
		
		product.delete();
	}
	

	@Transactional(readOnly = true)
	@Override
	public List<ProductListResponseDto> getSellerProducts(Long memberId) {
	    Seller seller = sellerRepository.findWithMemberByMemberId(memberId)
	            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLER));
	    return productRepository.findBySellerIdAndDeletedAtIsNull(seller.getSellerId())
	            .stream()
	            .map(ProductListResponseDto::from)
	            .toList();
	}

}
