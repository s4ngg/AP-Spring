package co.kr.allpick.domain.product.controller.docs;

import co.kr.allpick.domain.product.dto.ChildCategoryResponseDto;
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

@Tag(name = "Child Categories", description = "자식 카테고리 관련 API")
public interface ChildCategoryControllerDocs {

    @Operation(summary = "노출 중인 자식 카테고리 목록 조회", description = "부모 카테고리 ID로 노출 중인 자식 카테고리를 sort_order 순으로 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "자식 카테고리 목록을 조회합니다.",
                        "data": [
                            {
                                "childCategoryId": 1,
                                "parentCategoryId": 1,
                                "categoryName": "스킨케어",
                                "sortOrder": 1,
                                "isActive": 1,
                                "slug": "skincare"
                            }
                        ]
                    }
                """)
                    )
            )
    })
    @GetMapping("/{parentCategoryId}/child-categories")
    ResponseEntity<ApiResponse<List<ChildCategoryResponseDto>>> getActiveChildCategories(
            @Parameter(description = "부모 카테고리 ID", example = "1", required = true)
            @PathVariable("parentCategoryId") Long parentCategoryId
    );

    @Operation(summary = "slug로 자식 카테고리 단건 조회", description = "slug를 이용해 자식 카테고리 단건을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "자식 카테고리를 조회합니다.",
                        "data": {
                            "childCategoryId": 1,
                            "parentCategoryId": 1,
                            "categoryName": "스킨케어",
                            "sortOrder": 1,
                            "isActive": 1,
                            "slug": "skincare"
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
    @GetMapping("/child-categories/{slug}")
    ResponseEntity<ApiResponse<ChildCategoryResponseDto>> getChildCategoryBySlug(
            @Parameter(description = "조회할 카테고리 slug", example = "skincare", required = true)
            @PathVariable("slug") String slug
    );
}