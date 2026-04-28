package co.kr.allpick.domain.member.sms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.allpick.domain.member.sms.controller.docs.SmsControllerDocs;
import co.kr.allpick.domain.member.sms.dto.SmsRequestDto;
import co.kr.allpick.domain.member.sms.dto.SmsVerifyRequestDto;
import co.kr.allpick.domain.member.sms.service.SmsService;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsController implements SmsControllerDocs {

    private final SmsService smsService;

    @Override
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendCode(
            @Valid @RequestBody SmsRequestDto dto) {
        smsService.sendVerificationCode(dto.getPhone());
        return ApiResponse.success("인증번호가 발송되었습니다.");
    }

    @Override
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyCode(
            @Valid @RequestBody SmsVerifyRequestDto dto) {
        smsService.verifyCode(dto.getPhone(), dto.getCode());
        return ApiResponse.success("인증이 완료되었습니다.");
    }
}