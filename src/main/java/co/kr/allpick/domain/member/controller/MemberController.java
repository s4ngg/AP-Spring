package co.kr.allpick.domain.member.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import co.kr.allpick.domain.member.docs.MemberControllerDocs;
import co.kr.allpick.domain.member.dto.MemberResponseDto;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.member.service.MemberService;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Override
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponseDto>> getMember(
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        return ApiResponse.success("회원 정보 조회 성공", memberService.getMember(userInfo.getMemberId()));
    }
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkEmail(
            @RequestParam("email") String email) {
        boolean exists = memberService.checkEmailDuplicate(email);
        return ApiResponse.success("이메일 확인 완료", Map.of("available", !exists));
    }
}
