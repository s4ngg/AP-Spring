package co.kr.allpick.domain.seller.service.impl;

import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.seller.dto.SellerLoginRequestDto;
import co.kr.allpick.domain.seller.dto.SellerLoginResponseDto;
import co.kr.allpick.domain.seller.dto.SellerSignupRequestDto;
import co.kr.allpick.domain.seller.dto.SellerUpdateRequestDto;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.domain.seller.service.SellerAuthService;
import co.kr.allpick.global.config.JwtProvider;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerAuthServiceImpl implements SellerAuthService {

    private static final Logger logger = LogManager.getLogger(SellerAuthServiceImpl.class);

    private final SellerRepository sellerRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public void update(Long sellerId, SellerUpdateRequestDto dto) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_NOT_FOUND));
        seller.updateInfo(
                dto.getBusinessName(),
                dto.getRepresentativeName(),
                dto.getBankName(),
                dto.getBankAccount()
        );
        logger.info("[SellerAuthService] 판매자 정보 수정 완료 - sellerId: {}", sellerId);
    }

    @Override
    public void deleteSeller(Long sellerId) {
        Seller seller = sellerRepository.findBySellerIdAndDeletedAtIsNull(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_NOT_FOUND));
        seller.delete();
        sellerRepository.save(seller);
    }
    @Override
    public void signup(SellerSignupRequestDto dto, Long memberId) {
        if (sellerRepository.existsByBusinessNumber(dto.getBusinessNumber())) {
            logger.warn("[SellerAuthService] 사업자등록번호 중복 - businessNumber: {}", dto.getBusinessNumber());
            throw new BusinessException(ErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (sellerRepository.existsByMemberId(memberId)) {
            logger.warn("[SellerAuthService] 이미 판매자 등록된 회원 - memberId: {}", memberId);
            throw new BusinessException(ErrorCode.SELLER_ALREADY_EXISTS);
        }

        Seller seller = dto.toEntity(member);
        sellerRepository.save(seller);
        logger.info("[SellerAuthService] 판매자 등록 완료 - memberId: {}", memberId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SellerLoginResponseDto login(SellerLoginRequestDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> {
                    logger.warn("[SellerAuthService] 존재하지 않는 이메일로 로그인 시도");
                    return new BusinessException(ErrorCode.INVALID_PASSWORD);
                });

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            logger.warn("[SellerAuthService] 비밀번호 불일치 - memberId: {}", member.getId());
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        Seller seller = sellerRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLER));

        String token = jwtProvider.createToken(member.toJwtUserInfoDto());
        logger.info("[SellerAuthService] 판매자 로그인 성공 - sellerId: {}", seller.getSellerId());

        return SellerLoginResponseDto.of(seller, token);
    }
}