package co.kr.allpick.global.config;

import co.kr.allpick.domain.admin.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminJwtUserInfoDto {
    private Long adminId;
    private String email;
    private Admin.AdminRole role;
}