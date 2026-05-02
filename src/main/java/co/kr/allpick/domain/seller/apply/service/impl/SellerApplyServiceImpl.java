package co.kr.allpick.domain.seller.apply.service.impl;

import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.seller.apply.dto.SellerApplyRequestDto;
import co.kr.allpick.domain.seller.apply.dto.SellerApplyStatusResponseDto;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.domain.seller.apply.service.SellerApplyService;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerApplyServiceImpl implements SellerApplyService {

    private static final Logger logger = LogManager.getLogger(SellerApplyServiceImpl.class);

    private final SellerRepository sellerRepository;
    private final MemberRepository memberRepository;

    @Override
    public void apply(SellerApplyRequestDto dto, Long memberId) {  // ← apply 메서드 시작
    	if (sellerRepository.existsByMemberId(memberId)) {
            logger.warn("[SellerApplyService] 이미 판매자 신청된 회원 - memberId: {}", memberId);
            throw new BusinessException(ErrorCode.SELLER_ALREADY_EXISTS);
        }
    	if (sellerRepository.existsByBusinessNumber(dto.getBusinessNumber())) {
            logger.warn("[SellerApplyService] 사업자등록번호 중복 - businessNumber: {}", dto.getBusinessNumber());
            throw new BusinessException(ErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }
        

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // ✅ 여기에 추가
        Seller seller = Seller.of(member, dto);
        sellerRepository.save(seller);
        logger.info("[SellerApplyService] 판매자 신청 완료 - memberId: {}", memberId);

    }
    @Override
    @Transactional(readOnly = true)
    public SellerApplyStatusResponseDto getApplyStatus(Long memberId) {
        Seller seller = sellerRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_NOT_FOUND));
        return SellerApplyStatusResponseDto.of(seller);
    }
   
}