package co.kr.allpick.domain.member.sms;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import co.kr.allpick.global.response.ApiResponse;
import co.kr.allpick.domain.member.service.SmsService;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsController implements SmsControllerDocs {

    private final SmsService smsService;

    
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendCode(
            @Valid @RequestBody SmsRequestDto dto) {
        smsService.sendVerificationCode(dto.getPhone());
        return ApiResponse.success("인증번호가 발송되었습니다.");
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyCode(
            @Valid @RequestBody SmsVerifyRequestDto dto) {
        smsService.verifyCode(dto.getPhone(), dto.getCode());
        return ApiResponse.success("인증이 완료되었습니다.");
    }
}