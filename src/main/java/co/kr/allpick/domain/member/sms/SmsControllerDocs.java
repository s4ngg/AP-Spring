package co.kr.allpick.domain.member.sms;

import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "SMS 인증", description = "SMS API")
public interface SmsControllerDocs {

    @Operation(summary = "SMS 인증번호 발송", description = "입력한 번호로 6자리 인증번호를 발송합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "발송 성공",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "인증번호가 발송되었습니다.",
                    "data": null
                }
            """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "SMS 발송 실패",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "SMS 발송에 실패했습니다.",
                    "data": null
                }
            """)))
    })
    ResponseEntity<ApiResponse<Void>> sendCode(@RequestBody @Valid SmsRequestDto dto);

    @Operation(summary = "SMS 인증번호 확인", description = "발송된 인증번호를 확인합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증 성공",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "인증이 완료되었습니다.",
                    "data": null
                }
            """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "인증번호 불일치",
            content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "잘못된 입력값입니다.",
                    "data": null
                }
            """)))
    })
    ResponseEntity<ApiResponse<Void>> verifyCode(@RequestBody @Valid SmsVerifyRequestDto dto);
}