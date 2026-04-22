package co.kr.allpick.domain.member.service;

import co.kr.allpick.domain.member.dto.AuthResponseDto;
import co.kr.allpick.domain.member.dto.LoginRequestDto;
import co.kr.allpick.domain.member.dto.SignupRequestDto;

public interface AuthService {
    void signup(SignupRequestDto dto);
    AuthResponseDto login(LoginRequestDto dto);
}