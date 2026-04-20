package co.kr.allpick.domain.member.docs;

import co.kr.allpick.domain.member.dto.AuthResponseDto;
import co.kr.allpick.domain.member.dto.LoginRequestDto;
import co.kr.allpick.domain.member.dto.SignupRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;   // Swagger ApiResponse만 import
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "회원 인증 API")
public interface AuthControllerDocs {

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름, 전화번호, 주소로 회원가입합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = co.kr.allpick.global.response.ApiResponse.class))),
        @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일",
            content = @Content(schema = @Schema(implementation = co.kr.allpick.global.response.ApiResponse.class)))
    })
    ResponseEntity<co.kr.allpick.global.response.ApiResponse<Void>> signup(
        @RequestBody @Valid SignupRequestDto dto);

    @Operation(summary = "로그인", description = "이메일, 비밀번호로 로그인 후 JWT 토큰을 반환합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 틀림",
            content = @Content(schema = @Schema(implementation = co.kr.allpick.global.response.ApiResponse.class)))
    })
    ResponseEntity<co.kr.allpick.global.response.ApiResponse<AuthResponseDto>> login(
        @RequestBody @Valid LoginRequestDto dto);
}