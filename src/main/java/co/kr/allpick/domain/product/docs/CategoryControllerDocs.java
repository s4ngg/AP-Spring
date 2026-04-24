package co.kr.allpick.domain.product.docs;

import co.kr.allpick.domain.product.dto.CategoryProductResponseDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "카테고리", description = "카테고리 기준 상품 조회 API")
public interface CategoryControllerDocs {

    @Operation(
            summary = "카테고리별 상품 목록 조회",
            description = "특정 카테고리에 속한 상품 목록을 조회합니다."
    )
    ResponseEntity<ApiResponse<List<CategoryProductResponseDto>>> getProductsByCategory(
            @Parameter(description = "카테고리 ID", example = "1")
            @PathVariable("categoryId") Long categoryId
    );
}