package co.kr.allpick.domain.product.controller.docs;

import co.kr.allpick.domain.product.dto.ProductDetailResponseDto;
import co.kr.allpick.domain.product.dto.ProductSaveRequestDto;
import co.kr.allpick.domain.product.dto.ProductSaveResponseDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Product", description = "상품 관련 API")
public interface ProductControllerDocs {

    @Operation(summary = "상품 등록", description = "카테고리 ID, 상품 정보, 옵션 및 이미지 리스트를 받아 새로운 상품을 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "상품 등록 성공",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "상품을 생성했습니다.",
                    "data": {
                        "productId": 2,
                        "message": "상품 등록에 성공했습니다."
                    }
                }
            """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (중복 상품명 등)",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "이미 사용 중인 상품명입니다.",
                    "data": null
                }
            """)))
    })
    ResponseEntity<ApiResponse<ProductSaveResponseDto>> createProduct(
        @RequestBody @Valid ProductSaveRequestDto productSaveRequestDto);

    @Operation(summary = "상품 상세 조회", description = "상품 ID로 유효한 상품(판매중 & 승인완료)의 상세 정보를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 조회 성공",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "상품을 조회합니다.",
                    "data": {
                        "productId": 1,
                        "parentCategoryName": "주류",
                        "brand": "나이키",
                        "productName": "에어맥스 97 화이트",
                        "thumbnailUrl": "https://allpick.com",
                        "price": 25000,
                        "description": "나이키 에어맥스 97의 클래식 화이트 컬러입니다...",
                        "manufacturer": "나이키 코리아",
                        "origin": "베트남",
                        "precaution": "직사광선을 피해 보관하세요",
                        "optionList": [
                            {
                                "optionId": 1,
                                "optionName": "사이즈",
                                "optionValue": "270",
                                "additionalPrice": 0,
                                "stockQuantity": 50
                            }
                        ],
                        "productImagesList": [
                            {
                                "productImageId": 1,
                                "imageUrl": "https://allpick.com",
                                "sortOrder": 1
                            }
                        ]
                    }
                }
            """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "해당 상품이 존재하지 않거나 판매 중이 아닙니다.",
                    "data": null
                }
            """)))
    })
    ResponseEntity<ApiResponse<ProductDetailResponseDto>> getProductDetail(
        @PathVariable("productId") Long productId);
}