package co.kr.allpick.domain.admin.dto;

import co.kr.allpick.domain.admin.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginResponseDto {

    private String adminName;
    private Admin.AdminRole role;
    private String token;

    public static AdminLoginResponseDto from(Admin admin, String token) {
        return AdminLoginResponseDto.builder()
                .adminName(admin.getAdminName())
                .role(admin.getRole())
                .token(token)
                .build();
    }
}
