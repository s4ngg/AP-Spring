package co.kr.allpick.domain.seller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "판매자 로그인 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SellerLoginRequestDto {

    @NotBlank
    @Email
    @Schema(description = "이메일", example = "test@test.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank
    @Schema(description = "비밀번호", example = "Password1234!",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}