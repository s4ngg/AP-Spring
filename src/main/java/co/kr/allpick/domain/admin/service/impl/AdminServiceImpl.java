package co.kr.allpick.domain.admin.service.impl;

import co.kr.allpick.domain.admin.dto.AdminCreateRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginResponseDto;
import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.admin.repository.AdminRepository;
import co.kr.allpick.domain.admin.service.AdminService;
import co.kr.allpick.global.config.JwtProvider;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LogManager.getLogger(AdminServiceImpl.class);

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public AdminLoginResponseDto adminLogin(AdminLoginRequestDto adminLoginRequestDto) {
        Admin admin = adminRepository.findByEmail(adminLoginRequestDto.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        if (!passwordEncoder.matches(adminLoginRequestDto.getPassword(), admin.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        JwtUserInfoDto jwtUserInfoDto = new JwtUserInfoDto(
                admin.getAdminId(),
                admin.getEmail()
        );

        String token = jwtProvider.createToken(jwtUserInfoDto);
        admin.updateLastLoginAt(LocalDateTime.now());

        logger.info("[AdminServiceImpl] 관리자 로그인 성공 - adminId: {}", admin.getAdminId());
        return AdminLoginResponseDto.from(admin, token);
    }

    @Override
    @Transactional
    public void createAdmin(AdminCreateRequestDto adminCreateRequestDto) {
        if (adminRepository.existsByEmail(adminCreateRequestDto.getEmail())) {
            throw new BusinessException(ErrorCode.ADMIN_EMAIL_DUPLICATED);
        }

        String encodedPassword = passwordEncoder.encode(adminCreateRequestDto.getPassword());
        Admin admin = adminCreateRequestDto.toEntity(encodedPassword);

        adminRepository.save(admin);

        logger.info("[AdminServiceImpl] 관리자 등록 완료 - adminId: {}", admin.getAdminId());
    }

    @Override
    @Transactional
    public void updateStatus(Long adminId, Admin.AdminStatus status) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        admin.updateStatus(status);

        logger.info("[AdminServiceImpl] 관리자 상태 변경 - adminId: {}, status: {}", adminId, status);
    }
}
