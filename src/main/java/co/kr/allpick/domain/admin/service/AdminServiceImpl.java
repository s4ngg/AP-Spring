package co.kr.allpick.domain.admin.service;


import co.kr.allpick.domain.admin.dto.AdminCreateRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginResponseDto;
import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.admin.repository.AdminRepository;
import co.kr.allpick.global.config.JwtProvider;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public AdminLoginResponseDto adminLogin(AdminLoginRequestDto adminLoginRequestDto) {


        Admin admin =
                adminRepository.findByEmail(adminLoginRequestDto.getEmail())
                        .orElseThrow(() -> new
                                BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        if (!passwordEncoder.matches(adminLoginRequestDto.getPassword(), admin.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        JwtUserInfoDto jwtUserInfoDto = new JwtUserInfoDto(
                admin.getAdminId(),
                admin.getEmail(),
                admin.getRole().name()
        );

        String token = jwtProvider.createToken(jwtUserInfoDto);


        return AdminLoginResponseDto.builder()
                .adminName(admin.getAdminName())
                .role(admin.getRole().name())
                .token(token)
                .build();

    }

    @Override
    public void createAdmin(AdminCreateRequestDto adminCreateRequestDto) {

        //1. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(adminCreateRequestDto.getPassword());

        //2. Admin 객체 생성(toEntity()패턴)
        Admin admin = adminCreateRequestDto.toEntity(encodedPassword);

        adminRepository.save(admin);


    }

    @Override
    public void updateStatus(Long adminId, Admin.AdminStatus status) {

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));
        admin.updateStatus(status);
    }
}
