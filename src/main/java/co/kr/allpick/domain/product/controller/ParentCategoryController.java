package co.kr.allpick.domain.product.controller;


import co.kr.allpick.domain.product.controller.docs.ParentCategoryControllerDocs;
import co.kr.allpick.domain.product.dto.ParentCategoryResponseDto;
import co.kr.allpick.domain.product.service.ParentCategoryService;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class ParentCategoryController implements ParentCategoryControllerDocs {

    private final ParentCategoryService parentCategoryService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<ParentCategoryResponseDto>>>
    getActiveParentCategories() {
        return ApiResponse.success("부모 카테고리 목록을 조회합니다.",
                parentCategoryService.getActiveParentCategories());
    }

    @Override
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ParentCategoryResponseDto>>
    getParentCategoryBySlug(@PathVariable("slug") String slug) {
        return ApiResponse.success("부모 카테고리를 조회합니다.",
                parentCategoryService.getParentCategoryBySlug(slug));
    }



}
