package co.kr.allpick.domain.seller.controller.docs;

import co.kr.allpick.domain.seller.dto.SellerLoginRequestDto;
import co.kr.allpick.domain.seller.dto.SellerLoginResponseDto;
import co.kr.allpick.domain.seller.dto.SellerSignupRequestDto;
import co.kr.allpick.domain.seller.dto.SellerUpdateRequestDto;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "SellerAuth", description = "판매자 인증 API")
public interface SellerAuthControllerDocs {

    @Operation(summary = "판매자 등록")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "판매자 등록 성공",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "판매자 등록이 완료되었습니다.",
                    "data": null
                }
            """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 등록된 사업자등록번호",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "이미 등록된 사업자등록번호입니다.",
                    "data": null
                }
            """)))
    })
    ResponseEntity<ApiResponse<Void>> signup(
            @RequestBody SellerSignupRequestDto dto,
            @AuthenticationPrincipal JwtUserInfoDto userInfo); // ✅ Long → JwtUserInfoDto
    @Operation(summary = "판매자 정보 수정")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "판매자 정보가 수정되었습니다.",
                    "data": null
                }
            """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "판매자 없음",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "판매자 권한이 없습니다.",
                    "data": null
                }
            """)))
    })
    ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Long sellerId,
            @RequestBody SellerUpdateRequestDto dto);
    @Operation(summary = "판매자 로그인")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "로그인 성공",
                    "data": null
                }
            """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "이메일 또는 비밀번호가 틀렸습니다.",
                    "data": null
                }
            """)))
    })
    
    ResponseEntity<ApiResponse<SellerLoginResponseDto>> login(
            @RequestBody SellerLoginRequestDto dto);
}