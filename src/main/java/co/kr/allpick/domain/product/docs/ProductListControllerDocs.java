package co.kr.allpick.domain.product.docs;

import co.kr.allpick.domain.product.dto.ProductListResponseDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "상품", description = "상품 리스트 API")
public interface ProductListControllerDocs {
	
     // 상품리스트 태그 관리
    @Operation(
            summary = "상품 목록 조회",
            description = "상품 리스트 페이지에 노출할 상품 목록을 조회합니다."
    )
    ResponseEntity<ApiResponse<List<ProductListResponseDto>>> getProductList();
}