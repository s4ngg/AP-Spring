package co.kr.allpick.domain.admin.dto;

import co.kr.allpick.domain.admin.entity.Admin;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateRequestDto {

    @Schema(description = "관리자 이메일", example = "admin@allpick.com")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Schema(description = "관리자 비밀번호", example = "Admin1234!")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Schema(description = "관리자 이름", example = "이소정")
    @NotBlank(message = "관리자 이름은 필수입니다.")
    private String adminName;

    @Schema(description = "관리자 연락처", example = "01012341234")
    @NotBlank(message = "전화번호는 필수입니다.")
    private String adminPhone;

    public Admin toEntity(String encodedPassword) {
        return Admin.builder()
                .adminName(this.getAdminName())
                .email(this.getEmail())
                .password(encodedPassword)
                .adminPhone(this.getAdminPhone())
                .status(Admin.AdminStatus.ACTIVE)
                .build();
    }
}
