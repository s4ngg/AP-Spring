package co.kr.allpick.domain.product.controller;

import co.kr.allpick.domain.product.controller.docs.ChildCategoryControllerDocs;
import co.kr.allpick.domain.product.dto.ChildCategoryResponseDto;
import co.kr.allpick.domain.product.service.ChildCategoryService;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class ChildCategoryController implements ChildCategoryControllerDocs {

    private final ChildCategoryService childCategoryService;

    @Override
    @GetMapping("/{parentCategoryId}/child-categories")
    public ResponseEntity<ApiResponse<List<ChildCategoryResponseDto>>> getActiveChildCategories(
            @PathVariable("parentCategoryId") Long parentCategoryId) {
        return ApiResponse.success("자식 카테고리 목록을 조회합니다.",
                childCategoryService.getActiveChildCategories(parentCategoryId));
    }

    @Override
    @GetMapping("/child-categories/{slug}")
    public ResponseEntity<ApiResponse<ChildCategoryResponseDto>> getChildCategoryBySlug(
            @PathVariable("slug") String slug) {
        return ApiResponse.success("자식 카테고리를 조회합니다.",
                childCategoryService.getChildCategoryBySlug(slug));
    }
}