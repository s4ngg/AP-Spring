package co.kr.allpick.domain.member.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import co.kr.allpick.domain.member.dto.AuthResponseDto;
import co.kr.allpick.domain.member.dto.LoginRequestDto;
import co.kr.allpick.domain.member.dto.SignupRequestDto;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import co.kr.allpick.global.config.JwtProvider;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LogManager.getLogger(AuthServiceImpl.class);

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 회원가입
    @Override
    public void signup(SignupRequestDto dto) {
        if (memberRepository.existsByEmail(dto.getEmail())) {
            logger.warn("[AuthService] 이메일 중복 - email: {}", dto.getEmail());
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        Member member = Member.createLocal(
            dto.getEmail(),
            passwordEncoder.encode(dto.getPassword()),
            dto.getName(),
            dto.getPhone(),
            dto.getAddress()
        );
        memberRepository.save(member);
        logger.info("[AuthService] 회원가입 완료 - memberId: {}", member.getId());
    }

    // 로그인
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
        String token = jwtProvider.createToken(member.toJwtUserInfoDto());
        return AuthResponseDto.of(token, member);
    }
}