package co.kr.allpick.domain.admin.category.controller.docs;

import co.kr.allpick.domain.admin.category.dto.AdminCategoryRequestDto;
import co.kr.allpick.domain.product.dto.ChildCategoryResponseDto;
import co.kr.allpick.domain.product.dto.ParentCategoryResponseDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Admin Category", description = "관리자 카테고리 관리 API")
public interface AdminCategoryControllerDocs {

    @Operation(summary = "대분류 목록 조회")
    ResponseEntity<ApiResponse<List<ParentCategoryResponseDto>>> getParentCategories(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo
    );

    @Operation(summary = "대분류 등록")
    ResponseEntity<ApiResponse<ParentCategoryResponseDto>> createParentCategory(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @RequestBody @Valid AdminCategoryRequestDto request
    );

    @Operation(summary = "대분류 수정")
    ResponseEntity<ApiResponse<ParentCategoryResponseDto>> updateParentCategory(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("parentCategoryId") Long parentCategoryId,
            @RequestBody @Valid AdminCategoryRequestDto request
    );

    @Operation(summary = "대분류 삭제")
    ResponseEntity<ApiResponse<Void>> deleteParentCategory(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("parentCategoryId") Long parentCategoryId
    );

    @Operation(summary = "소분류 목록 조회")
    ResponseEntity<ApiResponse<List<ChildCategoryResponseDto>>> getChildCategories(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("parentCategoryId") Long parentCategoryId
    );

    @Operation(summary = "소분류 등록")
    ResponseEntity<ApiResponse<ChildCategoryResponseDto>> createChildCategory(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("parentCategoryId") Long parentCategoryId,
            @RequestBody @Valid AdminCategoryRequestDto request
    );

    @Operation(summary = "소분류 수정")
    ResponseEntity<ApiResponse<ChildCategoryResponseDto>> updateChildCategory(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("childCategoryId") Long childCategoryId,
            @RequestBody @Valid AdminCategoryRequestDto request
    );

    @Operation(summary = "소분류 삭제")
    ResponseEntity<ApiResponse<Void>> deleteChildCategory(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("childCategoryId") Long childCategoryId
    );
}
