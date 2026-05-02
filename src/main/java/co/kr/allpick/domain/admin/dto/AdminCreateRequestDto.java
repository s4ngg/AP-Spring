package co.kr.allpick.domain.admin.dto;

import co.kr.allpick.domain.admin.entity.Admin;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCreateRequestDto {

    @Schema(description = "관리자 이메일", example = "admin@allpick.com")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Schema(description = "관리자 비밀번호", example = "Admin1234!")
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 최소 8자, 영문+숫자+특수문자 조합이어야 합니다."
    )
    private String password;

    @Schema(description = "관리자 이름", example = "이소정")
    @NotBlank(message = "관리자 이름은 필수입니다.")
    private String adminName;

    @Schema(description = "관리자 연락처", example = "01012341234")
    @NotBlank(message = "전화번호는 필수입니다.")
    private String adminPhone;

    @Schema(description = "관리자 권한", example = "SUPER_ADMIN")
    @NotBlank(message = "관리자 권한은 필수입니다.")
    @Pattern(regexp = "SUPER_ADMIN|CS_ADMIN", message = "role은 SUPER_ADMIN 또는 CS_ADMIN이어야 합니다.")
    private String role;

    public Admin toEntity(String encodedPassword) {
        return Admin.builder()
                .adminName(this.getAdminName())
                .email(this.getEmail())
                .password(encodedPassword)
                .adminPhone(this.getAdminPhone())
                .role(Admin.AdminRole.valueOf(this.getRole()))
                .status(Admin.AdminStatus.ACTIVE)
                .build();
    }
}
