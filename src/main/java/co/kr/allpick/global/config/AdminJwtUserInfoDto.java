package co.kr.allpick.global.config;

import co.kr.allpick.domain.admin.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminJwtUserInfoDto {
    private Long adminId;
    private String email;
    private Admin.AdminRole role;

    public static AdminJwtUserInfoDto from(Admin admin) {
        return new AdminJwtUserInfoDto(
                admin.getAdminId(),
                admin.getEmail(),
                admin.getRole()
        );
    }
}
