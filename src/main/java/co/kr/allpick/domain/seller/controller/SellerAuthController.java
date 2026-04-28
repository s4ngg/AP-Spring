package co.kr.allpick.domain.seller.controller;

import co.kr.allpick.domain.seller.controller.docs.SellerAuthControllerDocs;
import co.kr.allpick.domain.seller.dto.SellerLoginRequestDto;
import co.kr.allpick.domain.seller.dto.SellerLoginResponseDto;
import co.kr.allpick.domain.seller.dto.SellerSignupRequestDto;
import co.kr.allpick.domain.seller.service.SellerAuthService;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seller/auth")
@RequiredArgsConstructor
public class SellerAuthController implements SellerAuthControllerDocs {

    private final SellerAuthService sellerAuthService;

    @Override
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(
            @RequestBody SellerSignupRequestDto dto,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        sellerAuthService.signup(dto, userInfo.getMemberId());
        return ApiResponse.success("판매자 등록이 완료되었습니다."); // ✅ ResponseEntity.ok() 제거
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<SellerLoginResponseDto>> login(
            @RequestBody SellerLoginRequestDto dto) {
        return ApiResponse.success("로그인 성공", sellerAuthService.login(dto)); // ✅ ResponseEntity.ok() 제거
    }
}