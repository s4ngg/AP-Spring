package co.kr.allpick.domain.admin.service;

import co.kr.allpick.domain.admin.dto.AdminCreateRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginResponseDto;
import co.kr.allpick.domain.admin.entity.Admin;

public interface AdminService {

    AdminLoginResponseDto adminLogin(AdminLoginRequestDto adminLoginRequestDto);

    void createAdmin(AdminCreateRequestDto adminCreateRequestDto);

    void  updateStatus(Long adminId, Admin.AdminStatus status);
}