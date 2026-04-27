package co.kr.allpick.domain.seller.product.service;

import java.util.List;
import java.util.Map;

public interface SellerProductDetailService {
    void saveDetails(Long productId, Map<String, String> details);
    List<Map<String, String>> getDetails(Long productId);
    void updateDetail(Long productId, String detailKey, String detailValue);
    void deleteDetails(Long productId);
}