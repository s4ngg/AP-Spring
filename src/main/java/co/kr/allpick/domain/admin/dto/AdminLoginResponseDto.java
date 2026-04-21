package co.kr.allpick.domain.admin.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminLoginResponseDto {

    private String adminName;
    private String token;
    private String role;
}
