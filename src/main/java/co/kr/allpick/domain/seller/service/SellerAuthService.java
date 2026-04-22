package co.kr.allpick.domain.seller.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.seller.dto.SellerLoginRequestDto;
import co.kr.allpick.domain.seller.dto.SellerLoginResponseDto;
import co.kr.allpick.domain.seller.dto.SellerSignupRequestDto;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.global.config.JwtProvider;      // 프로젝트 내 기존 JwtProvider 사용
import co.kr.allpick.domain.seller.dto.SellerUpdateRequestDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerAuthService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder  passwordEncoder;
    private final JwtProvider      jwtProvider;       // 기존 JWT 재사용

    // ── 회원가입 ───────────────────────────────────────────
    @Transactional
    public void signup(SellerSignupRequestDto req) {
        if (sellerRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (sellerRepository.existsByBusinessNumber(req.getBusinessNumber())) {
            throw new IllegalArgumentException("이미 등록된 사업자등록번호입니다.");
        }

        Seller seller = Seller.builder()
            .email(req.getEmail())
            .password(passwordEncoder.encode(req.getPassword()))
            .name(req.getName())
            .phone(req.getPhone())
            .address(req.getAddress())
            .businessNumber(req.getBusinessNumber())
            .businessName(req.getBusinessName())
            .build();                                  // status 기본값 1 (활성)

        sellerRepository.save(seller);
    }

    // ── 로그인 ─────────────────────────────────────────────
    @Transactional(readOnly = true)
    public SellerLoginResponseDto login(SellerLoginRequestDto req) {
        // status=1 (활성) 인 판매자만 조회
        Seller seller = sellerRepository.findByEmailAndStatus(req.getEmail(), 1)
            .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(req.getPassword(), seller.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 기존 JwtProvider 재사용 (toJwtUserInfoDto()로 "SELLER" role 포함)
        String token = jwtProvider.createToken(seller.toJwtUserInfoDto());

        return SellerLoginResponseDto.of(seller, token);
    }

    // ── 정보 수정 ──────────────────────────────────────────
    @Transactional
    public void update(Long sellerId, SellerUpdateRequestDto req) {
        Seller seller = sellerRepository.findById(sellerId)
            .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));

        seller.update(req.getName(), req.getPhone(), req.getAddress(), req.getBusinessName());
    }

    // ── 탈퇴 (Soft Delete) ─────────────────────────────────
    @Transactional
    public void delete(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
            .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));

        seller.delete();   // status = 0
    }
}