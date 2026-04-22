package co.kr.allpick.domain.seller.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class SellerSignupRequestDto {

    @NotBlank @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "전화번호를 입력해주세요.")
    private String phone;

    @NotBlank(message = "주소를 입력해주세요.")
    private String address;

    @NotBlank(message = "사업자등록번호를 입력해주세요.")
    @Pattern(regexp = "\\d{10}", message = "사업자등록번호는 10자리 숫자입니다.")
    private String businessNumber;

    @NotBlank(message = "상호명을 입력해주세요.")
    private String businessName;
}