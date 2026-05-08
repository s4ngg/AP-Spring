package co.kr.allpick.domain.seller.service.impl;

import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.seller.dto.SellerLoginRequestDto;
import co.kr.allpick.domain.seller.dto.SellerLoginResponseDto;
import co.kr.allpick.domain.seller.dto.SellerSignupRequestDto;
import co.kr.allpick.domain.seller.dto.SellerUpdateRequestDto;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.entity.SellerStatus;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.domain.seller.service.SellerAuthService;
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
    public void update(Long sellerId, SellerUpdateRequestDto dto, Long memberId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_NOT_FOUND));

        validateSellerOwner(seller, memberId);

        seller.updateInfo(
                dto.getBusinessName(),
                dto.getRepresentativeName(),
                dto.getBankName(),
                dto.getBankAccount()
        );
        logger.info("[SellerAuthService] ?êÎß§???ïÎ≥¥ ?òÏ†ï ?ÑÎ£å - sellerId: {}", sellerId);
    }

    @Override
    public void deleteSeller(Long sellerId, Long memberId) {
        Seller seller = sellerRepository.findBySellerIdAndDeletedAtIsNull(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_NOT_FOUND));

        validateSellerOwner(seller, memberId);  // ???¨Ìçº Î©îÏÑú?úÎ°ú ÍµêÏ≤¥

        seller.delete();
        sellerRepository.save(seller);
    }
    @Override
    public void signup(SellerSignupRequestDto dto, Long memberId) {
    	if (sellerRepository.existsByMember_Id(memberId)) {
            logger.warn("[SellerAuthService] ?¥Î? ?êÎß§???±Î°ù???åÏõê - memberId: {}", memberId);
            throw new BusinessException(ErrorCode.SELLER_ALREADY_EXISTS);
        }
    	
    	if (sellerRepository.existsByBusinessNumber(dto.getBusinessNumber())) {
            logger.warn("[SellerAuthService] ?¨ÏóÖ?êÎì±Î°ùÎ≤à??Ï§ëÎ≥µ - businessNumber: {}", dto.getBusinessNumber());
            throw new BusinessException(ErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        

        Seller seller = dto.toEntity(member);
        sellerRepository.save(seller);
        logger.info("[SellerAuthService] ?êÎß§???±Î°ù ?ÑÎ£å - memberId: {}", memberId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SellerLoginResponseDto login(SellerLoginRequestDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> {
                    logger.warn("[SellerAuthService] Ï°¥Ïû¨?òÏ? ?äÎäî ?¥Î©î?ºÎ°ú Î°úÍ∑∏???úÎèÑ");
                    return new BusinessException(ErrorCode.INVALID_PASSWORD);
                });

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            logger.warn("[SellerAuthService] ÎπÑÎ?Î≤àÌò∏ Î∂àÏùºÏπ?- memberId: {}", member.getId());
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
        if (!Integer.valueOf(1).equals(member.getStatus())) {
            logger.warn("[SellerAuthService] ?ïÏ? ?åÏõê ?êÎß§??Î°úÍ∑∏???úÎèÑ - memberId: {}", member.getId());
            throw new BusinessException(ErrorCode.MEMBER_BLOCKED);
        }
        Seller seller = sellerRepository.findByMember_IdAndDeletedAtIsNull(member.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLER));

        if (seller.getStatus() != SellerStatus.APPROVED) {
            throw new BusinessException(ErrorCode.NOT_SELLER);
        }

        String token = jwtProvider.createToken(JwtUserInfoDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .build());
        logger.info("[SellerAuthService] ?êÎß§??Î°úÍ∑∏???±Í≥µ - sellerId: {}", seller.getSellerId());

        return SellerLoginResponseDto.of(seller, token);
    }
    private void validateSellerOwner(Seller seller, Long memberId) {
        if (!seller.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }
}
