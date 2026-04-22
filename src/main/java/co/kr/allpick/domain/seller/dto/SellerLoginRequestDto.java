package co.kr.allpick.domain.seller.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class SellerLoginRequestDto {

    @NotBlank @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}