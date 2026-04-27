package co.kr.allpick.domain.seller.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.seller.dto.SellerAuthResponseDto;  // ← 변경
import co.kr.allpick.domain.seller.dto.SellerLoginRequestDto;
import co.kr.allpick.domain.seller.dto.SellerSignupRequestDto;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.global.config.JwtProvider;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import co.kr.allpick.global.service.BusinessValidationService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerAuthServiceImpl implements SellerAuthService {

    private static final Logger logger = LogManager.getLogger(SellerAuthServiceImpl.class);

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final BusinessValidationService businessValidationService;

    // 판매자 회원가입
    @Override
    public void signup(SellerSignupRequestDto dto) {
        if (sellerRepository.existsByEmail(dto.getEmail())) {
            logger.warn("[SellerAuthService] 이메일 중복 - email: {}", dto.getEmail());
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (!businessValidationService.validateBusinessNumber(dto.getBusinessNumber())) {
            logger.warn("[SellerAuthService] 유효하지 않은 사업자등록번호 - {}", dto.getBusinessNumber());
            throw new BusinessException(ErrorCode.INVALID_BUSINESS_NUMBER);
        }
        sellerRepository.save(dto.toEntity(passwordEncoder.encode(dto.getPassword())));
        logger.info("[SellerAuthService] 판매자 회원가입 완료");
    }

    // 판매자 로그인
    @Override
    @Transactional(readOnly = true)
    public SellerAuthResponseDto login(SellerLoginRequestDto dto) {  // ← 변경
        Seller seller = sellerRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> {
                    logger.warn("[SellerAuthService] 존재하지 않는 이메일로 판매자 로그인 시도");
                    return new BusinessException(ErrorCode.INVALID_PASSWORD);
                });

        if (!passwordEncoder.matches(dto.getPassword(), seller.getPassword())) {
            logger.warn("[SellerAuthService] 판매자 비밀번호 불일치 - sellerId: {}", seller.getId());
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        logger.info("[SellerAuthService] 판매자 로그인 성공 - sellerId: {}", seller.getId());
        String token = jwtProvider.createToken(seller.toJwtUserInfoDto());
        return SellerAuthResponseDto.of(token, seller);  // ← 변경
    }
}