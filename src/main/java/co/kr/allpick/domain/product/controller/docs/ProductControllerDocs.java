package co.kr.allpick.domain.product.controller.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import co.kr.allpick.domain.product.dto.ProductDetailResDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Product", description = "상품 관련 API")
public interface ProductControllerDocs {

    @Operation(summary = "상품 상세 조회", description = "상품 ID를 이용해 상세 정보를 조회합니다.")
    ResponseEntity<ApiResponse<ProductDetailResDto>> getProductDetail(
        @Parameter(description = "조회할 상품 ID", example = "1") 
        @PathVariable Long productId
    );
}