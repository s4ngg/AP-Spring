package co.kr.allpick.domain.admin.dto;

import co.kr.allpick.domain.admin.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateRequestDto {

    private String email;
    private String password;
    private String adminName;
    private String adminPhone;
    private String role;
}

