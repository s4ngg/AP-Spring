package co.kr.allpick.domain.member.service.impl;

import co.kr.allpick.domain.member.service.AuthService;
import co.kr.allpick.domain.member.sms.service.SmsService;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.domain.seller.entity.Seller;
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
    private final SellerRepository sellerRepository;
    @Override
    public void signup(SignupRequestDto dto) {
        if (!smsService.isVerified(dto.getPhone())) {
            throw new BusinessException(ErrorCode.PHONE_NOT_VERIFIED);
        }
        if (memberRepository.existsByEmail(dto.getEmail())) {
            logger.warn("[AuthService] ?´ë©”??ì¤‘ë³µ - email: {}");
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        memberRepository.save(dto.toEntity(passwordEncoder.encode(dto.getPassword())));
        smsService.removeVerified(dto.getPhone());
        logger.info("[AuthService] ?Œì›ê°€???„ë£Œ");
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
                    logger.warn("[AuthService] ì¡´ì¬?˜ì? ?ŠëŠ” ?´ë©”?¼ë¡œ ë¡œê·¸???œë„");
                    return new BusinessException(ErrorCode.INVALID_PASSWORD);
                });
        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            logger.warn("[AuthService] ë¹„ë?ë²ˆí˜¸ ë¶ˆì¼ì¹?- memberId: {}", member.getId());
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
        if (!Integer.valueOf(1).equals(member.getStatus())) {
            logger.warn("[AuthService] ?•ì? ?Œì› ë¡œê·¸???œë„ - memberId: {}", member.getId());
            throw new BusinessException(ErrorCode.MEMBER_BLOCKED);
        }
        logger.info("[AuthService] ë¡œê·¸???±ê³µ - memberId: {}", member.getId());

        String token = jwtProvider.createToken(JwtUserInfoDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .build());

        boolean isSeller = sellerRepository.findByMember_Id(member.getId()).isPresent();

        return AuthResponseDto.of(token, member, isSeller);
    }
}
