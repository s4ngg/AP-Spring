package co.kr.allpick.domain.seller.product.service;

import co.kr.allpick.domain.seller.product.entity.SellerProduct;
import co.kr.allpick.domain.seller.product.entity.SellerProductDetail;
import co.kr.allpick.domain.seller.product.repository.SellerProductDetailRepository;
import co.kr.allpick.domain.seller.product.repository.SellerProductRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerProductDetailServiceImpl implements SellerProductDetailService {

    private final SellerProductDetailRepository detailRepository;
    private final SellerProductRepository productRepository;

    // 상세정보 저장
    @Override
    public void saveDetails(Long productId, Map<String, String> details) {
        SellerProduct product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        details.forEach((key, value) -> {
            SellerProductDetail detail = SellerProductDetail.builder()
                    .product(product)
                    .detailKey(key)
                    .detailValue(value)
                    .build();
            detailRepository.save(detail);
        });
    }

    // 상세정보 조회
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, String>> getDetails(Long productId) {
        return detailRepository.findByProductId(productId)
                .stream()
                .map(d -> Map.of(d.getDetailKey(), d.getDetailValue()))
                .collect(Collectors.toList());
    }

    // 상세정보 수정
    @Override
    public void updateDetail(Long productId, String detailKey, String detailValue) {
        SellerProductDetail detail = detailRepository
                .findByProductIdAndDetailKey(productId, detailKey)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        detail.update(detailValue);
    }

    // 상세정보 전체 삭제
    @Override
    public void deleteDetails(Long productId) {
        detailRepository.deleteByProductId(productId);
    }
}