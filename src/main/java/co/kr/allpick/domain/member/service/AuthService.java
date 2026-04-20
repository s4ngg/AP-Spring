package co.kr.allpick.domain.member.service;

import co.kr.allpick.domain.member.dto.AuthResponseDto;
import co.kr.allpick.domain.member.dto.LoginRequestDto;
import co.kr.allpick.domain.member.dto.SignupRequestDto;
import co.kr.allpick.domain.member.dto.SignupSellerRequestDto;

public interface AuthService {
    void signup(SignupRequestDto dto);
    void signupSeller(SignupSellerRequestDto dto);
    AuthResponseDto login(LoginRequestDto dto);
    AuthResponseDto loginSeller(LoginRequestDto dto);
}