package co.kr.allpick.domain.product.controller.docs;

import co.kr.allpick.domain.product.dto.ParentCategoryResponseDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Parent Categories", description = "부모 카테고리 관련 API")
public interface ParentCategoryControllerDocs {

    @Operation(summary = "노출 중인 부모 카테고리 전체 조회", description = "is_active = 1인 부모 카테고리를 sort_order 순으로 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "부모 카테고리 목록을 조회합니다.",
                        "data": [
                            {
                                "parentCategoryId": 1,
                                "categoryName": "뷰티",
                                "sortOrder": 1,
                                "isActive": 1,
                                "slug": "beauty"
                            }
                        ]
                    }
                """)
                    )
            )
    })
    @GetMapping
    ResponseEntity<ApiResponse<List<ParentCategoryResponseDto>>> getActiveParentCategories();

    @Operation(summary = "slug로 부모 카테고리 단건 조회", description = "slug를 이용해 부모 카테고리 단건을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "부모 카테고리를 조회합니다.",
                        "data": {
                            "parentCategoryId": 1,
                            "categoryName": "뷰티",
                            "sortOrder": 1,
                            "isActive": 1,
                            "slug": "beauty"
                        }
                    }
                """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "카테고리 없음",
                    content = @Content(
                            examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "message": "존재하지 않는 카테고리입니다.",
                        "data": null
                    }
                """)
                    )
            )
    })
    @GetMapping("/{slug}")
    ResponseEntity<ApiResponse<ParentCategoryResponseDto>> getParentCategoryBySlug(
            @Parameter(description = "조회할 카테고리 slug", example = "beauty", required = true)
            @PathVariable("slug") String slug
    );
}