package co.kr.allpick.domain.seller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SellerLoginRequestDto {

    private String email;
    private String password;
}