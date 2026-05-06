package co.kr.allpick.domain.seller.controller;


import co.kr.allpick.domain.seller.controller.docs.SellerAuthControllerDocs;
import co.kr.allpick.domain.seller.dto.SellerDeleteRequestDto;
import co.kr.allpick.domain.seller.dto.SellerLoginRequestDto;
import co.kr.allpick.domain.seller.dto.SellerLoginResponseDto;
import co.kr.allpick.domain.seller.dto.SellerSignupRequestDto;
import co.kr.allpick.domain.seller.dto.SellerUpdateRequestDto;
import co.kr.allpick.domain.seller.service.SellerAuthService;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            @RequestBody @Valid SellerSignupRequestDto dto,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        sellerAuthService.signup(dto, userInfo.getMemberId());
        return ApiResponse.success("판매자 등록이 완료되었습니다.");
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<SellerLoginResponseDto>> login(
            @RequestBody @Valid SellerLoginRequestDto dto) {
        return ApiResponse.success("로그인 성공", sellerAuthService.login(dto));
    }
    
    @Override
    @PatchMapping("/{sellerId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @RequestBody @Valid SellerUpdateRequestDto dto,
            @PathVariable("sellerId") Long sellerId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        sellerAuthService.update(sellerId, dto, userInfo.getMemberId());
        return ApiResponse.success("판매자 정보가 수정되었습니다.");
    }
    
    @Override
    @DeleteMapping("/{sellerId}")
    public ResponseEntity<ApiResponse<Void>> deleteSeller(
            @PathVariable("sellerId") Long sellerId, 
    		@AuthenticationPrincipal JwtUserInfoDto userInfo) {
    	sellerAuthService.deleteSeller(sellerId, userInfo.getMemberId());
        return ApiResponse.success("판매자 삭제가 완료되었습니다.");
    }
}