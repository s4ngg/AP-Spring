package co.kr.allpick.domain.product.controller;

import co.kr.allpick.domain.product.docs.CategoryControllerDocs;
import co.kr.allpick.domain.product.dto.CategoryProductResponseDto;
import co.kr.allpick.domain.product.service.CategoryService;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController implements CategoryControllerDocs {

    private final CategoryService categoryService;

    @Override
    @GetMapping("/{categoryId}/products")
    public ResponseEntity<ApiResponse<List<CategoryProductResponseDto>>> getProductsByCategory(
            @PathVariable("categoryId") Long categoryId
    ) {
        List<CategoryProductResponseDto> products = categoryService.getProductsByCategory(categoryId);
        return ApiResponse.success("카테고리별 상품 조회 성공", products);
    }
}