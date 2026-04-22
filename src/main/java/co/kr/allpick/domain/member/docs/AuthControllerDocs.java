package co.kr.allpick.domain.member.docs;

import co.kr.allpick.domain.member.dto.AuthResponseDto;
import co.kr.allpick.domain.member.dto.LoginRequestDto;
import co.kr.allpick.domain.member.dto.SignupRequestDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "회원 인증 API")
public interface AuthControllerDocs {

    @Operation(summary = "일반 회원가입", description = "이메일, 비밀번호, 이름, 전화번호, 주소로 회원가입합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "회원가입 성공",
                    "data": null
                }
            """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "이미 사용 중인 이메일입니다.",
                    "data": null
                }
            """)))
    })
    ResponseEntity<ApiResponse<Void>> signup(
        @RequestBody @Valid SignupRequestDto dto);

    @Operation(summary = "로그인", description = "이메일, 비밀번호로 로그인 후 JWT 토큰을 반환합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "로그인 성공",
                    "data": {
                        "token": "eyJhbGciOiJIUzI1NiJ9...",
                        "email": "test@test.com",
                        "name": "홍길동"
                    }
                }
            """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 틀림",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "이메일 또는 비밀번호가 틀렸습니다.",
                    "data": null
                }
            """)))
    })
    ResponseEntity<ApiResponse<AuthResponseDto>> login(
        @RequestBody @Valid LoginRequestDto dto);

}