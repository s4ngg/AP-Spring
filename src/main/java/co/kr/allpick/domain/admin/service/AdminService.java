package co.kr.allpick.domain.admin.service;

import co.kr.allpick.domain.admin.dto.AdminCreateRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginResponseDto;
import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;

public interface AdminService {

    AdminLoginResponseDto adminLogin(AdminLoginRequestDto request);

    void createAdmin(AdminJwtUserInfoDto adminInfo, AdminCreateRequestDto request);

    void updateStatus(AdminJwtUserInfoDto adminInfo, Long adminId, Admin.AdminStatus status);
}
