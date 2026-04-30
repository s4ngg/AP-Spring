package co.kr.allpick.domain.admin.service;

import co.kr.allpick.domain.admin.dto.AdminCreateRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginResponseDto;
import co.kr.allpick.domain.admin.entity.Admin;

public interface AdminService {

    AdminLoginResponseDto adminLogin(AdminLoginRequestDto request);

    void createAdmin(Long actorAdminId, AdminCreateRequestDto request);

    void updateStatus(Long actorAdminId, Long adminId, Admin.AdminStatus status);
}
