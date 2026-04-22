package co.kr.allpick.domain.seller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SellerUpdateRequestDto {

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "전화번호를 입력해주세요.")
    private String phone;

    @NotBlank(message = "주소를 입력해주세요.")
    private String address;

    @NotBlank(message = "상호명을 입력해주세요.")
    private String businessName;
}