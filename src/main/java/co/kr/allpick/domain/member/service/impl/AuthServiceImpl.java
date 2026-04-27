package co.kr.allpick.domain.member.service.impl;

import co.kr.allpick.domain.member.service.AuthService;
import co.kr.allpick.domain.member.service.SmsService;
import co.kr.allpick.domain.member.repository.MemberTermsRepository;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.member.dto.AuthResponseDto;
import co.kr.allpick.domain.member.dto.LoginRequestDto;
import co.kr.allpick.domain.member.dto.SignupRequestDto;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.entity.MemberTerms;
import co.kr.allpick.domain.member.entity.Terms;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.member.repository.TermsRepository;
import co.kr.allpick.global.config.JwtProvider;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LogManager.getLogger(AuthServiceImpl.class);

    private final MemberRepository memberRepository;
    private final TermsRepository termsRepository;           // ✅ 추가
    private final MemberTermsRepository memberTermsRepository; // ✅ 추가
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final SmsService smsService;

    @Override
    public void signup(SignupRequestDto dto) {
        try {
            if (!smsService.isVerified(dto.getPhone())) {
                throw new BusinessException(ErrorCode.PHONE_NOT_VERIFIED);
            }
            if (memberRepository.existsByEmail(dto.getEmail())) {
                logger.warn("[AuthService] 이메일 중복 - email: {}", dto.getEmail());
                throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
            }

            Member member = memberRepository.save(
                dto.toEntity(passwordEncoder.encode(dto.getPassword()))
            );

            List<Terms> termsList = termsRepository.findAllById(dto.getAgreedTermsIds()); // ✅ 소문자
            List<MemberTerms> memberTermsList = termsList.stream()
                .map(terms -> MemberTerms.builder()
                    .member(member)
                    .terms(terms)
                    .isAgreed(true)
                    .agreedAt(LocalDateTime.now())
                    .build())
                .toList();

            memberTermsRepository.saveAll(memberTermsList);
            smsService.removeVerified(dto.getPhone());
            logger.info("[AuthService] 회원가입 완료");

        } catch (BusinessException e) {
            throw e; // BusinessException은 그대로 던지기
        } catch (Exception e) {
            logger.error("[AuthService] 회원가입 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto dto) {
        try {
            Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> {
                    logger.warn("[AuthService] 존재하지 않는 이메일로 로그인 시도");
                    return new BusinessException(ErrorCode.INVALID_PASSWORD);
                });

            if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
                logger.warn("[AuthService] 비밀번호 불일치 - memberId: {}", member.getId());
                throw new BusinessException(ErrorCode.INVALID_PASSWORD);
            }

            String token = jwtProvider.createToken(member.toJwtUserInfoDto());
            logger.info("[AuthService] 로그인 성공 - memberId: {}", member.getId());
            return AuthResponseDto.of(token, member);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("[AuthService] 로그인 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}