package co.kr.allpick.domain.member.service;

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
import co.kr.allpick.global.config.JwtUserInfoDto;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 회원가입
    @Override
    public void signup(SignupRequestDto dto) {
        if (memberRepository.existsByEmail(dto.getEmail())) {
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
    }

    // 로그인
    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_PASSWORD));

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        JwtUserInfoDto jwtUserInfoDto = new JwtUserInfoDto(
            member.getId(),
            member.getEmail(),
            member.getRole().name()  // enum → String 변환
        );
        String token = jwtProvider.createToken(jwtUserInfoDto);
        return AuthResponseDto.of(token, member);
    }
}