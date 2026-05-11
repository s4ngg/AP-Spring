package co.kr.allpick.domain.admin.category.controller;

import co.kr.allpick.domain.admin.category.controller.docs.AdminCategoryControllerDocs;
import co.kr.allpick.domain.admin.category.dto.AdminCategoryRequestDto;
import co.kr.allpick.domain.admin.category.service.AdminCategoryService;
import co.kr.allpick.domain.product.dto.ChildCategoryResponseDto;
import co.kr.allpick.domain.product.dto.ParentCategoryResponseDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class AdminCategoryController implements AdminCategoryControllerDocs {

    private final AdminCategoryService adminCategoryService;

    @Override
    @GetMapping("/parents")
    public ResponseEntity<ApiResponse<List<ParentCategoryResponseDto>>> getParentCategories(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo) {
        return ApiResponse.success("대분류 목록 조회 성공", adminCategoryService.getParentCategories(adminInfo));
    }

    @Override
    @PostMapping("/parents")
    public ResponseEntity<ApiResponse<ParentCategoryResponseDto>> createParentCategory(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @RequestBody @Valid AdminCategoryRequestDto request) {
        return ApiResponse.success("대분류 등록 성공", adminCategoryService.createParentCategory(adminInfo, request));
    }

    @Override
    @PatchMapping("/parents/{parentCategoryId}")
    public ResponseEntity<ApiResponse<ParentCategoryResponseDto>> updateParentCategory(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("parentCategoryId") Long parentCategoryId,
            @RequestBody @Valid AdminCategoryRequestDto request) {
        return ApiResponse.success("대분류 수정 성공",
                adminCategoryService.updateParentCategory(adminInfo, parentCategoryId, request));
    }

    @Override
    @DeleteMapping("/parents/{parentCategoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteParentCategory(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("parentCategoryId") Long parentCategoryId) {
        adminCategoryService.deleteParentCategory(adminInfo, parentCategoryId);
        return ApiResponse.success("대분류 삭제 성공");
    }

    @Override
    @GetMapping("/parents/{parentCategoryId}/children")
    public ResponseEntity<ApiResponse<List<ChildCategoryResponseDto>>> getChildCategories(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("parentCategoryId") Long parentCategoryId) {
        return ApiResponse.success("소분류 목록 조회 성공",
                adminCategoryService.getChildCategories(adminInfo, parentCategoryId));
    }

    @Override
    @PostMapping("/parents/{parentCategoryId}/children")
    public ResponseEntity<ApiResponse<ChildCategoryResponseDto>> createChildCategory(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("parentCategoryId") Long parentCategoryId,
            @RequestBody @Valid AdminCategoryRequestDto request) {
        return ApiResponse.success("소분류 등록 성공",
                adminCategoryService.createChildCategory(adminInfo, parentCategoryId, request));
    }

    @Override
    @PatchMapping("/children/{childCategoryId}")
    public ResponseEntity<ApiResponse<ChildCategoryResponseDto>> updateChildCategory(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("childCategoryId") Long childCategoryId,
            @RequestBody @Valid AdminCategoryRequestDto request) {
        return ApiResponse.success("소분류 수정 성공",
                adminCategoryService.updateChildCategory(adminInfo, childCategoryId, request));
    }

    @Override
    @DeleteMapping("/children/{childCategoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteChildCategory(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("childCategoryId") Long childCategoryId) {
        adminCategoryService.deleteChildCategory(adminInfo, childCategoryId);
        return ApiResponse.success("소분류 삭제 성공");
    }
}
