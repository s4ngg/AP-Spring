// SmsVerifyRequestDto.java
package co.kr.allpick.global.sms;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "SMS 인증번호 확인 요청")
public class SmsVerifyRequestDto {

    @Schema(description = "전화번호", example = "01037560740")
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^01[016789]\\d{7,8}$",
            message = "전화번호는 하이픈 없이 입력해주세요. 예) 01012341234")
    private String phone;

    @Schema(description = "인증번호", example = "123456")
    @NotBlank(message = "인증번호는 필수입니다.")
    @Pattern(regexp = "^\\d{6}$", message = "인증번호는 6자리 숫자입니다.")
    private String code;
}