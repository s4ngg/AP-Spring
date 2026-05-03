package co.kr.allpick.domain.cart.controller.docs;

import co.kr.allpick.domain.cart.dto.CartItemDeleteRequestDto;
import co.kr.allpick.domain.cart.dto.CartItemRequestDto;
import co.kr.allpick.domain.cart.dto.CartItemResponseDto;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Cart", description = "장바구니 API")
public interface CartItemControllerDocs {

    @Operation(summary = "장바구니 상품 추가",
               description = "사용자의 장바구니에 새로운 상품을 추가합니다. JWT 토큰으로 회원을 식별합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "장바구니 추가 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CartItemResponseDto.class),
                examples = @ExampleObject(name = "성공 응답 예시", value = """
                    {
                        "success": true,
                        "message": "장바구니에 상품이 추가되었습니다.",
                        "data": {
                            "cartItemId": 1,
                            "brandName": "나이키",
                            "productName": "에어포스",
                            "price": 17000,
                            "option": "270",
                            "quantity": 2
                        }
                    }
                """)
            )),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (필수값 누락)",
            content = @Content(
                examples = @ExampleObject(name = "필드 누락 에러", value = """
                    {
                        "success": false,
                        "message": "상품 옵션 ID는 필수입니다.",
                        "data": null
                    }
                """)
            ))
    })
    @PostMapping
    ResponseEntity<ApiResponse<CartItemResponseDto>> addCart(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestBody @Valid CartItemRequestDto reqDto);

    @Operation(summary = "장바구니 목록 조회", description = "JWT 토큰으로 식별된 회원의 장바구니 상품 목록을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "장바구니 조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "목록 조회 예시", value = """
                    {
                        "success": true,
                        "message": "장바구니 목록을 성공적으로 불러왔습니다.",
                        "data": [
                            {
                                "cartItemId": 1,
                                "brandName": "나이키",
                                "productName": "에어포스",
                                "price": 17000,
                                "option": "270",
                                "quantity": 2
                            }
                        ]
                    }
                """)
            ))
    })
    @GetMapping("/{memberId}")
    ResponseEntity<ApiResponse<List<CartItemResponseDto>>> getCartItem(
            @AuthenticationPrincipal JwtUserInfoDto userInfo);

    @Operation(summary = "장바구니 상품 단건 삭제", description = "장바구니에서 특정 상품을 삭제합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "제거했습니다.",
                        "data": null
                    }
                """)
            ))
    })
    @DeleteMapping("/{cartItemId}")
    ResponseEntity<ApiResponse<Void>> deleteCartItem(
            @PathVariable("cartItemId") Long cartItemId);

    @Operation(summary = "장바구니 상품 선택 삭제", description = "선택한 여러 장바구니 상품을 삭제합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "선택 삭제 성공",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "선택한 상품을 제거했습니다.",
                        "data": null
                    }
                """)
            ))
    })
    @DeleteMapping("/selected")
    ResponseEntity<ApiResponse<Void>> deleteSelectedCartItems(
            @RequestBody CartItemDeleteRequestDto deleteDto);

    @Operation(summary = "장바구니 전체 비우기", description = "JWT 토큰으로 식별된 회원의 장바구니를 전체 비웁니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "전체 삭제 성공",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "모든 상품을 제거했습니다.",
                        "data": null
                    }
                """)
            ))
    })
    @DeleteMapping("/clear")
    ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal JwtUserInfoDto userInfo);
}