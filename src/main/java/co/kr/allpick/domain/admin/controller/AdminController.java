package co.kr.allpick.domain.admin.controller;

import co.kr.allpick.domain.admin.dto.AdminCreateRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginResponseDto;
import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.admin.controller.docs.AdminControllerDocs;
import co.kr.allpick.domain.admin.service.AdminService;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController implements AdminControllerDocs {

    private final AdminService adminService;

    @PostMapping("/login")
    @Override
    public ResponseEntity<ApiResponse<AdminLoginResponseDto>> adminLogin(
            @RequestBody @Valid AdminLoginRequestDto request) {
        return ApiResponse.success("관리자 로그인 성공", adminService.adminLogin(request));
    }

    @PostMapping
    @Override
    public ResponseEntity<ApiResponse<Void>> createAdmin(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @RequestBody @Valid AdminCreateRequestDto request) {
        adminService.createAdmin(request);
        return ApiResponse.success("관리자 등록 성공");
    }

    @PatchMapping("/{adminId}/status")
    @Override
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("adminId") Long adminId,
            @RequestParam("status") Admin.AdminStatus status) {
        adminService.updateStatus(adminId, status);
        return ApiResponse.success("관리자 상태 변경 성공");
    }
}
