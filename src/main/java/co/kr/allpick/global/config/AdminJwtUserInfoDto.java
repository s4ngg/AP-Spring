package co.kr.allpick.global.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminJwtUserInfoDto {
    private Long adminId;
    private String email;
    private String role;
}