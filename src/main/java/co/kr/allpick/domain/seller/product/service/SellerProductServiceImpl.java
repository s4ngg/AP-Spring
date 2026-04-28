package co.kr.allpick.domain.seller.product.service;

import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.product.dto.SellerProductRequestDto;
import co.kr.allpick.domain.seller.product.dto.SellerProductResponseDto;
import co.kr.allpick.domain.seller.product.entity.SellerProduct;
import co.kr.allpick.domain.seller.product.repository.SellerProductRepository;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerProductServiceImpl implements SellerProductService {

    private static final Logger logger = LogManager.getLogger(SellerProductServiceImpl.class);

    private final SellerProductRepository sellerProductRepository;
    private final SellerRepository sellerRepository;

    // 상품 등록
    @Override
    public void createProduct(Long sellerId, SellerProductRequestDto dto) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        SellerProduct product = dto.toEntity(seller);
        sellerProductRepository.save(product);
        logger.info("[SellerProductService] 상품 등록 완료 - productId: {}", product.getId());
    }

    // 상품 전체 조회
    @Override
    @Transactional(readOnly = true)
    public List<SellerProductResponseDto> getProducts(Long sellerId) {
        return sellerProductRepository.findBySellerId(sellerId)
                .stream()
                .map(SellerProductResponseDto::of)
                .collect(Collectors.toList());
    }

    // 상품 카테고리별 조회
    @Override
    @Transactional(readOnly = true)
    public List<SellerProductResponseDto> getProductsByCategory(Long sellerId, SellerProduct.Category category) {
        return sellerProductRepository.findBySellerIdAndCategory(sellerId, category)
                .stream()
                .map(SellerProductResponseDto::of)
                .collect(Collectors.toList());
    }

    // 상품 검색
    @Override
    @Transactional(readOnly = true)
    public List<SellerProductResponseDto> searchProducts(Long sellerId, String name) {
        return sellerProductRepository.findBySellerIdAndNameContaining(sellerId, name)
                .stream()
                .map(SellerProductResponseDto::of)
                .collect(Collectors.toList());
    }

    // 상품 단건 조회
    @Override
    @Transactional(readOnly = true)
    public SellerProductResponseDto getProduct(Long sellerId, Long productId) {
        SellerProduct product = sellerProductRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        return SellerProductResponseDto.of(product);
    }

    // 상품 수정
    @Override
    public void updateProduct(Long sellerId, Long productId, SellerProductRequestDto dto) {
        SellerProduct product = sellerProductRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.update(
                dto.getName(),
                dto.getCategory(),
                dto.getPrice(),
                dto.getStock(),
                dto.getDescription(),
                dto.getShortDescription(),
                dto.getThumbnailUrl()
        );
        logger.info("[SellerProductService] 상품 수정 완료 - productId: {}", productId);
    }

    // 상품 삭제
    @Override
    public void deleteProduct(Long sellerId, Long productId) {
        SellerProduct product = sellerProductRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.delete();
        logger.info("[SellerProductService] 상품 삭제 완료 - productId: {}", productId);
    }

    // 상품 상태 변경
    @Override
    public void changeProductStatus(Long sellerId, Long productId, SellerProduct.Status status) {
        SellerProduct product = sellerProductRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.changeStatus(status);
        logger.info("[SellerProductService] 상품 상태 변경 완료 - productId: {}, status: {}", productId, status);
    }
}