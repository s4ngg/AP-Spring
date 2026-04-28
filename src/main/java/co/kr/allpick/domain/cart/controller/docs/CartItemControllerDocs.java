package co.kr.allpick.domain.cart.controller.docs;

import co.kr.allpick.domain.cart.dto.CartItemRequestDto;
import co.kr.allpick.domain.cart.dto.CartItemResponseDto;
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

import java.util.List;

@Tag(name = "Cart", description = "장바구니 API")
public interface CartItemControllerDocs {

    @Operation(summary = "장바구니 상품 추가", description = "사용자의 장바구니에 새로운 상품을 추가합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "장바구니 추가 성공",
            content = @Content(
                examples = @ExampleObject(value = """
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
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "message": "상품 ID는 필수입니다.",
                        "data": null
                    }
                """)
            ))
    })
    ResponseEntity<ApiResponse<CartItemResponseDto>> addCart(
            @PathVariable("memberId") Long memberId,
            @RequestBody @Valid CartItemRequestDto reqDto);

    @Operation(summary = "장바구니 목록 조회", description = "특정 회원의 장바구니에 담긴 모든 상품 목록을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "장바구니 조회 성공",
            content = @Content(
                examples = @ExampleObject(value = """
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
                            },
                            {
                                "cartItemId": 2,
                                "brandName": "아디다스",
                                "productName": "슈퍼스타",
                                "price": 15000,
                                "option": "260",
                                "quantity": 1
                            }
                        ]
                    }
                """)
            ))
    })
    ResponseEntity<ApiResponse<List<CartItemResponseDto>>> getCartItem(
            @PathVariable("memberId") Long memberId);
}