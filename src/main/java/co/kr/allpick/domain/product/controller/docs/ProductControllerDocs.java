package co.kr.allpick.domain.product.controller.docs;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import co.kr.allpick.domain.product.dto.ProductDetailResDto;
import co.kr.allpick.domain.product.dto.ProductListResponseDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Products", description = "상품 관련 API")
public interface ProductControllerDocs {

    @Operation(summary = "상품 목록 조회", description = "판매 중이고 승인된 상품 목록을 페이징으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "상품 목록을 조회합니다.",
                        "data": {
                            "content": [
                                {
                                    "productId": 1,
                                    "parentCategoryName": "뷰티",
                                    "brand": "에스티로더",
                                    "productName": "갈색병 세럼 50ml",
                                    "thumbnailUrl": "https://allpick.com",
                                    "price": 89000
                                }
                            ],
                            "totalElements": 100,
                            "totalPages": 13,
                            "size": 8,
                            "number": 0
                        }
                    }
                """)
            )
        )
    })
    @GetMapping
    ResponseEntity<ApiResponse<Page<ProductListResponseDto>>> getProductList(
            @ParameterObject @PageableDefault(size = 8, sort = "createdAt") Pageable pageable
    );

    @Operation(summary = "상품 상세 조회", description = "상품 ID를 이용해 상품의 상세 정보를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "상품 상세 조회 성공",
                        "data": {
                            "productId": 1,
                            "productName": "나이키 운동화",
                            "price": 129000,
                            "brand": "나이키",
                            "description": "편안한 착용감의 런닝화입니다.",
                            "thumbnailUrl": "https://allpick.com",
                            "categoryName": "신발"
                        }
                    }
                """)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "상품을 찾을 수 없음",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "message": "해당 상품이 존재하지 않습니다.",
                        "data": null
                    }
                """)
            )
        )
    })
    @GetMapping("/{productId}")
    ResponseEntity<ApiResponse<ProductDetailResDto>> getProductDetail(
        @Parameter(description = "조회할 상품 ID", example = "1", required = true)
        @PathVariable("productId") Long productId
    );
}