package co.kr.allpick.domain.admin.controller.docs;

import co.kr.allpick.domain.admin.dto.AdminCreateRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginResponseDto;
import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Admin", description = "관리자 API")
public interface AdminControllerDocs {

    @Operation(summary = "관리자 로그인", description = "관리자 이메일과 비밀번호로 로그인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "관리자 로그인 성공",
                    "data": {
                        "token": "eyJhbGciOiJIUzI1NiJ9...",
                        "adminId": 1,
                        "email": "admin@allpick.kr",
                        "role": "SUPER_ADMIN"
                    }
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "관리자 없음")
    })
    ResponseEntity<ApiResponse<AdminLoginResponseDto>> adminLogin(
            @RequestBody @Valid AdminLoginRequestDto request);

    @Operation(summary = "관리자 등록", description = "SUPER_ADMIN 권한으로 신규 관리자를 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "등록 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "관리자 등록 성공",
                    "data": null
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "SUPER_ADMIN 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이메일 중복")
    })
    ResponseEntity<ApiResponse<Void>> createAdmin(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @RequestBody @Valid AdminCreateRequestDto request);

    @Operation(summary = "관리자 상태 변경", description = "SUPER_ADMIN 권한으로 관리자 계정 상태를 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상태 변경 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "관리자 상태 변경 성공",
                    "data": null
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "SUPER_ADMIN 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "관리자 없음")
    })
    ResponseEntity<ApiResponse<Void>> updateStatus(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("adminId") Long adminId,
            @RequestParam("status") Admin.AdminStatus status);
}
