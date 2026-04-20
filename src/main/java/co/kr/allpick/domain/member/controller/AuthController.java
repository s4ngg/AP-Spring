package co.kr.allpick.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.allpick.domain.member.service.AuthService;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import co.kr.allpick.domain.member.dto.AuthResponseDto;
import co.kr.allpick.domain.member.dto.LoginRequestDto;
import co.kr.allpick.domain.member.dto.SignupRequestDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	
	// 회원가입
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<Void>> signup(@RequestBody @Valid SignupRequestDto dto) {
		authService.signup(dto);
		return ApiResponse.success("회원가입 성공");
	}
	// 로그인
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponseDto>> login(@RequestBody @Valid LoginRequestDto dto) {
		AuthResponseDto response = authService.login(dto);
		return ApiResponse.success("로그인 성공", response);
	}
}
