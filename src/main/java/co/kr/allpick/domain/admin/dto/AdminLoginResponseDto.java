package co.kr.allpick.domain.admin.dto;

import co.kr.allpick.domain.admin.entity.Admin;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminLoginResponseDto {

    private String adminName;
    private String token;

    public static AdminLoginResponseDto from(Admin admin, String token){
        return AdminLoginResponseDto.builder()
                .adminName(admin.getAdminName())
                .token(token)
                .build();
    }
}
