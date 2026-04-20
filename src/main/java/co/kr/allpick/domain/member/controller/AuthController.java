package co.kr.allpick.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import co.kr.allpick.domain.member.docs.AuthControllerDocs;
import co.kr.allpick.domain.member.service.AuthService;
import co.kr.allpick.domain.member.dto.AuthResponseDto;
import co.kr.allpick.domain.member.dto.LoginRequestDto;
import co.kr.allpick.domain.member.dto.SignupRequestDto;
import co.kr.allpick.domain.member.dto.SignupSellerRequestDto;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    // 일반 회원가입
    @PostMapping("/signup")
    @Override
    public ResponseEntity<ApiResponse<Void>> signup(
            @RequestBody @Valid SignupRequestDto dto) {
        authService.signup(dto);
        return ApiResponse.success("회원가입 성공");
    }

    // 판매자 회원가입
    @PostMapping("/signup/seller")
    @Override
    public ResponseEntity<ApiResponse<Void>> signupSeller(
            @RequestBody @Valid SignupSellerRequestDto dto) {
        authService.signupSeller(dto);
        return ApiResponse.success("판매자 회원가입 성공");
    }

    // 일반 로그인
    @PostMapping("/login")
    @Override
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(
            @RequestBody @Valid LoginRequestDto dto) {
        AuthResponseDto response = authService.login(dto);
        return ApiResponse.success("로그인 성공", response);
    }

    // 판매자 로그인
    @PostMapping("/login/seller")
    @Override
    public ResponseEntity<ApiResponse<AuthResponseDto>> loginSeller(
            @RequestBody @Valid LoginRequestDto dto) {
        AuthResponseDto response = authService.loginSeller(dto);
        return ApiResponse.success("판매자 로그인 성공", response);
    }
}