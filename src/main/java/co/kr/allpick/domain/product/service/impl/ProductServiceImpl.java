package co.kr.allpick.domain.product.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import co.kr.allpick.domain.product.dto.ProductDetailResponseDto;
import co.kr.allpick.domain.product.dto.ProductListResponseDto;
import co.kr.allpick.domain.product.dto.ProductSaveRequestDto;
import co.kr.allpick.domain.product.dto.ProductSaveResponseDto;
import co.kr.allpick.domain.product.dto.ProductUpdateRequestDto;
import co.kr.allpick.domain.product.dto.ProductUpdateResponseDto;
import co.kr.allpick.domain.product.dto.SellerProductListResponseDto;
import co.kr.allpick.domain.product.entity.ChildCategory;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ChildCategoryRepository;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.domain.product.service.ProductService;
import co.kr.allpick.domain.review.dto.ReviewResponseDto;
import co.kr.allpick.domain.review.repository.ReviewRepository;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import co.kr.allpick.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);
    private final S3Uploader s3Uploader;
    
    private final ProductRepository productRepository;
    private final ChildCategoryRepository childCategoryRepository;
    private final ReviewRepository reviewRepository;
    private final SellerRepository sellerRepository;

    @Override
    public ProductSaveResponseDto createProduct(Long memberId, ProductSaveRequestDto reqDto) {
        Seller seller = sellerRepository.findWithMemberByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLER));
        ChildCategory childCategory = childCategoryRepository.findById(reqDto.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        if (productRepository.existsByProductName(reqDto.getProductName())) {
            throw new BusinessException(ErrorCode.PRODUCT_ALREADY_EXISTS);
        }
        Product product = reqDto.toEntity(seller, childCategory);
        product.assignParentCategory(childCategory.getParentCategory().getParentCategoryId());
        productRepository.save(product);
        return ProductSaveResponseDto.from(product);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductDetailResponseDto getProductDetail(Long productId, Pageable pageable) {
        Product product = productRepository.findValidProduct(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        Page<ReviewResponseDto> reviewPage = reviewRepository.findByProductId(productId, pageable)
                .map(ReviewResponseDto::from);
        return ProductDetailResponseDto.from(product, reviewPage);
    }

    @Override
    public ProductUpdateResponseDto updateProduct(Long memberId, Long productId, ProductUpdateRequestDto reqDto) {
        Product product = productRepository.findByProductIdAndMemberId(memberId, productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        product.update(reqDto);
        return ProductUpdateResponseDto.from(product);
    }

    @Override
    public String uploadImage(MultipartFile image) {
        return s3Uploader.upload(image, "products");
    }
    @Transactional(readOnly = true)
    @Override
    public Page<ProductListResponseDto> getProductList(Pageable pageable) {
        logger.info("상품 목록 조회 - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return productRepository.findVisibleProducts(
                Product.Status.ON_SALE,
                Product.ApprovalStatus.APPROVED,
                pageable
        ).map(ProductListResponseDto::from);
    }

    @Override
    public void deleteProduct(Long memberId, Long productId) {
        Product product = productRepository.findByProductIdAndMemberId(memberId, productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        product.delete();
    }

    @Transactional(readOnly = true)
    @Override
    public List<SellerProductListResponseDto> getSellerProducts(Long memberId) {
        Seller seller = sellerRepository.findWithMemberByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLER));
        return productRepository.findBySellerIdAndDeletedAtIsNull(seller.getSellerId())
                .stream()
                .map(SellerProductListResponseDto::from)
                .toList();
    }
}
