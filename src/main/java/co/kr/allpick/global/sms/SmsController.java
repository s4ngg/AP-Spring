package co.kr.allpick.global.sms;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import co.kr.allpick.global.response.ApiResponse;
import co.kr.allpick.domain.member.service.SmsService;

@Tag(name = "SMS Authentication", description = "SMS API")
@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    // 인증번호 발송
    @Operation(summary = "SMS 인증번호 발송")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendCode(
            @Valid @RequestBody SmsRequestDto dto) {
        smsService.sendVerificationCode(dto.getPhone());
        return ApiResponse.success("인증번호가 발송되었습니다.");
    }

    // 인증번호 확인
    @Operation(summary = "SMS 인증번호 확인")
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyCode(
            @Valid @RequestBody SmsVerifyRequestDto dto) {
        smsService.verifyCode(dto.getPhone(), dto.getCode());
        return ApiResponse.success("인증이 완료되었습니다.");
    }
}