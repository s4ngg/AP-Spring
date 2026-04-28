// SmsRequestDto.java
package co.kr.allpick.domain.member.sms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "SMS 인증번호 발송 요청")
public class SmsRequestDto {

    @Schema(description = "전화번호", example = "01037560740", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^01[016789]\\d{7,8}$",
            message = "전화번호는 하이픈 없이 입력해주세요. 예) 01012341234")
    private String phone;
}