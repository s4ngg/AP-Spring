package co.kr.allpick.domain.admin.controller.docs;

import co.kr.allpick.domain.admin.dto.AdminCreateRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginResponseDto;
import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Admin", description = "관리자 API")
public interface AdminControllerDocs {

    @Operation(summary = "관리자 로그인", description = "관리자 이메일과 비밀번호로 로그인합니다.")
    ResponseEntity<ApiResponse<AdminLoginResponseDto>> adminLogin(
            @RequestBody @Valid AdminLoginRequestDto request);

    @Operation(summary = "관리자 등록", description = "신규 관리자를 등록합니다.")
    ResponseEntity<ApiResponse<Void>> createAdmin(
            @RequestBody @Valid AdminCreateRequestDto request);

    @Operation(summary = "관리자 상태 변경", description = "관리자 계정 상태를 변경합니다.")
    ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable("adminId") Long adminId,
            @RequestParam("status") Admin.AdminStatus status);
}
