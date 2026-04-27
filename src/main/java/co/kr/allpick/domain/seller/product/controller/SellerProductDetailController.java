package co.kr.allpick.domain.seller.product.controller;

import co.kr.allpick.domain.seller.product.service.SellerProductDetailService;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "상품 상세정보", description = "상품 상세정보 API")
@RestController
@RequestMapping("/api/seller/products/{productId}/details")
@RequiredArgsConstructor
public class SellerProductDetailController {

    private final SellerProductDetailService detailService;

    // 상세정보 등록
    @Operation(summary = "상품 상세정보 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> saveDetails(
            @PathVariable Long productId,
            @RequestBody Map<String, String> details) {
        detailService.saveDetails(productId, details);
        return ApiResponse.success("상세정보 등록 성공");
    }

    // 상세정보 조회
    @Operation(summary = "상품 상세정보 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getDetails(
            @PathVariable Long productId) {
        return ApiResponse.success("상세정보 조회 성공", detailService.getDetails(productId));
    }

    // 상세정보 수정
    @Operation(summary = "상품 상세정보 수정")
    @PatchMapping("/{detailKey}")
    public ResponseEntity<ApiResponse<Void>> updateDetail(
            @PathVariable Long productId,
            @PathVariable String detailKey,
            @RequestBody Map<String, String> body) {
        detailService.updateDetail(productId, detailKey, body.get("value"));
        return ApiResponse.success("상세정보 수정 성공");
    }

    // 상세정보 삭제
    @Operation(summary = "상품 상세정보 삭제")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteDetails(
            @PathVariable Long productId) {
        detailService.deleteDetails(productId);
        return ApiResponse.success("상세정보 삭제 성공");
    }
}