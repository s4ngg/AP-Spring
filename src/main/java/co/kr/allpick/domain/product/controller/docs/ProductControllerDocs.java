package co.kr.allpick.domain.product.controller.docs;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;

import java.util.List;

import co.kr.allpick.domain.product.dto.ProductDetailResponseDto;
import co.kr.allpick.domain.product.dto.ProductListResponseDto;
import co.kr.allpick.domain.product.dto.ProductSaveRequestDto;
import co.kr.allpick.domain.product.dto.ProductSaveResponseDto;
import co.kr.allpick.domain.product.dto.ProductUpdateRequestDto;
import co.kr.allpick.domain.product.dto.ProductUpdateResponseDto;
import co.kr.allpick.domain.product.dto.SellerProductListResponseDto;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
@Tag(name = "Product", description = "상품 관련 API")
public interface ProductControllerDocs {

	@Operation(summary = "상품 등록", description = "카테고리 ID, 상품 정보 등을 받아 새로운 상품을 생성합니다. 토큰을 통해 판매자 정보를 식별합니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "상품 등록 성공", content = @Content(examples = @ExampleObject(value = """
					    {
					        "success": true,
					        "message": "상품을 생성했습니다.",
					        "data": {
					            "productId": 2,
					            "message": "상품 등록에 성공했습니다."
					        }
					    }
					"""))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (중복 상품명 등)") })
	@PostMapping
	ResponseEntity<ApiResponse<ProductSaveResponseDto>> createProduct(@AuthenticationPrincipal JwtUserInfoDto userInfo,
			@RequestBody @Valid ProductSaveRequestDto productSaveRequestDto);

	
	@PostMapping("/images")
	ResponseEntity<ApiResponse<String>> uploadImage(
	    @RequestPart("image") MultipartFile image);
	@Operation(summary = "상품 목록 조회", description = "판매 중이고 승인된 상품 목록을 페이징으로 조회합니다. 삭제된 상품은 제외됩니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "목록 조회 성공", content = @Content(examples = @ExampleObject(value = """
					    {
					        "success": true,
					        "message": "상품 목록을 조회했습니다.",
					        "data": {
					            "content": [
					                {
					                    "productId": 1,
					                    "productName": "나이키 에어맥스",
					                    "price": 129000,
					                    "thumbnailUrl": "https://allpick.com",
					                    "brand": "나이키",
					                    "status": "ON_SALE"
					                }
					            ],
					            "pageable": { "pageNumber": 0, "pageSize": 8 },
					            "totalElements": 1,
					            "totalPages": 1
					        }
					    }
					"""))) })
	@GetMapping
	ResponseEntity<ApiResponse<Page<ProductListResponseDto>>> getProductList(
			@ParameterObject @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable);

	@Operation(summary = "상품 상세 조회", description = "상품 ID로 상세 정보, 옵션, 이미지, 리뷰 목록(페이징)을 조회합니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상세 조회 성공", content = @Content(examples = @ExampleObject(value = """
					                {
					  "categoryId": 1,
					  "productName": "2번 원숭이",
					  "brand": "나이키",
					  "thumbnailUrl": "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&h=600&fit=crop",
					  "description": "나이키 에어맥스 97의 클래식 화이트 컬러입니다.",
					  "price": 27000,
					  "manufacturer": "나이키 코리아",
					  "origin": "베트남",
					  "precaution": "직사광선을 피해 보관하세요.",
					  "optionList": [
					    {
					      "optionName": "사이즈",
					      "optionValue": "270",
					      "additionalPrice": 0,
					      "stockQuantity": 5
					    }
					  ],
					  "productImageList": [
					    {
					      "imageUrl": "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600",
					      "sortOrder": 1
					    }
					  ]
					}
					            """))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 상품") })
	@GetMapping("/{productId}")
	ResponseEntity<ApiResponse<ProductDetailResponseDto>> getProductDetail(@PathVariable("productId") Long productId,
			@ParameterObject @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable);

	@Operation(summary = "상품 수정", description = "상품 정보를 수정합니다. 본인이 등록한 상품만 수정 가능합니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 수정 성공", content = @Content(examples = @ExampleObject(value = """
					    {
					        "success": true,
					        "message": "상품을 수정했습니다.",
					        "data": {
					            "productId": 1,
					            "message": "수정이 완료되었습니다."
					        }
					    }
					"""))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "수정 권한 없음") })
	@PatchMapping("/{productId}")
	ResponseEntity<ApiResponse<ProductUpdateResponseDto>> updateProduct(
			@AuthenticationPrincipal JwtUserInfoDto userInfo, @PathVariable("productId") Long productId,
			@RequestBody @Valid ProductUpdateRequestDto productUpdateRequestDto);

	@Operation(summary = "상품 삭제", description = "상품을 삭제 상태로 변경합니다. (소프트 딜리트 적용)")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 삭제 성공", content = @Content(examples = @ExampleObject(value = """
					    {
					        "success": true,
					        "message": "상품을 삭제했습니다.",
					        "data": null
					    }
					"""))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "삭제 권한 없음") })
	@DeleteMapping("/{productId}")
	ResponseEntity<ApiResponse<Void>> deleteProduct(@AuthenticationPrincipal JwtUserInfoDto userInfo,
			@PathVariable("productId") Long productId);

	@Operation(summary = "판매자 상품 목록 조회", description = "로그인한 판매자가 등록한 상품 목록과 승인 상태를 조회합니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "판매자 상품 목록 조회 성공"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "판매자 권한 없음") })
	@GetMapping("/seller")
	ResponseEntity<ApiResponse<List<SellerProductListResponseDto>>> getSellerProducts(
			@AuthenticationPrincipal JwtUserInfoDto userInfo);
}
