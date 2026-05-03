package co.kr.allpick.domain.product.controller.docs;

import co.kr.allpick.domain.product.dto.ProductDetailResponseDto;
import co.kr.allpick.domain.product.dto.ProductListResponseDto;
import co.kr.allpick.domain.product.dto.ProductSaveRequestDto;
import co.kr.allpick.domain.product.dto.ProductSaveResponseDto;
import co.kr.allpick.domain.product.dto.ProductUpdateRequestDto;
import co.kr.allpick.domain.product.dto.ProductUpdateResponseDto;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "Product", description = "상품 관련 API")
public interface ProductControllerDocs {

    @Operation(summary = "상품 등록", description = "카테고리 ID, 상품 정보 등을 받아 새로운 상품을 생성합니다. 토큰을 통해 판매자 정보를 식별합니다.")
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
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (중복 상품명 등)")
    })
    ResponseEntity<ApiResponse<ProductSaveResponseDto>> createProduct(
        @AuthenticationPrincipal JwtUserInfoDto userInfo, // [수정] JWT 정보 추가
        @RequestBody @Valid ProductSaveRequestDto productSaveRequestDto);

    @Operation(summary = "상품 목록 조회", description = "판매 중이고 승인된 상품 목록을 페이징으로 조회합니다.")
    @GetMapping
    ResponseEntity<ApiResponse<Page<ProductListResponseDto>>> getProductList(
            @ParameterObject @PageableDefault(size = 8, sort = "createdAt",
                                                direction = Sort.Direction.DESC) Pageable pageable);

    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상세 정보와 리뷰 목록(페이징)을 조회합니다.")
    @GetMapping("/{productId}")
    ResponseEntity<ApiResponse<ProductDetailResponseDto>> getProductDetail(
        @PathVariable("productId") Long productId,
        @ParameterObject @PageableDefault(size = 5, sort = "createdAt", 
                                            direction = Sort.Direction.DESC) Pageable pageable
    );

    @Operation(summary = "상품 수정", description = "상품 정보를 수정합니다. 본인이 등록한 상품만 수정 가능합니다.")
    ResponseEntity<ApiResponse<ProductUpdateResponseDto>> updateProduct(
        @AuthenticationPrincipal JwtUserInfoDto userInfo, // [추가] 본인 확인용
        @PathVariable("productId") Long productId,
        @RequestBody ProductUpdateRequestDto productUpdateRequestDto);

    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다. 본인이 등록한 상품만 삭제 가능합니다.")
    ResponseEntity<ApiResponse<Void>> deleteProduct(
        @AuthenticationPrincipal JwtUserInfoDto userInfo, // [추가] 본인 확인용
        @PathVariable("productId") Long productId);
}