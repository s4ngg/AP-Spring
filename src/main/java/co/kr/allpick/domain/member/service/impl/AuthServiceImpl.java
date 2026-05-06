package co.kr.allpick.domain.member.service.impl;

import co.kr.allpick.domain.member.service.AuthService;
import co.kr.allpick.domain.member.sms.service.SmsService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.kr.allpick.domain.member.dto.AuthResponseDto;
import co.kr.allpick.domain.member.dto.LoginRequestDto;
import co.kr.allpick.domain.member.dto.SignupRequestDto;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.global.config.JwtProvider;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LogManager.getLogger(AuthServiceImpl.class);
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final SmsService smsService;

    @Override
    public void signup(SignupRequestDto dto) {
//    	TODO: CoolSMS 연동 완료 후 주석 해제
//        if (!smsService.isVerified(dto.getPhone())) {
//            throw new BusinessException(ErrorCode.PHONE_NOT_VERIFIED);
//        }
        if (memberRepository.existsByEmail(dto.getEmail())) {
            logger.warn("[AuthService] 이메일 중복 - email: {}");
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        memberRepository.save(dto.toEntity(passwordEncoder.encode(dto.getPassword())));
        
//		TODO: CoolSMS 연동 완료 후 주석 해제
//        smsService.removeVerified(dto.getPhone());
        logger.info("[AuthService] 회원가입 완료");
    }

    @Override
    public String verifyAndFindId(String phoneNumber, String inputCode) {
        smsService.verifyCode(phoneNumber, inputCode);
        return memberRepository.findEmailByUserPhone(phoneNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_BY_PHONE));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> {
                    logger.warn("[AuthService] 존재하지 않는 이메일로 로그인 시도");
                    return new BusinessException(ErrorCode.INVALID_PASSWORD);
                });
        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            logger.warn("[AuthService] 비밀번호 불일치 - memberId: {}", member.getId());
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
        logger.info("[AuthService] 로그인 성공 - memberId: {}", member.getId());
        String token = jwtProvider.createToken(JwtUserInfoDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .build());
        return AuthResponseDto.of(token, member);
    }
}
