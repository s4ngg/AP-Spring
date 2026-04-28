package co.kr.allpick.domain.seller.controller.docs;

import co.kr.allpick.domain.seller.dto.SellerLoginRequestDto;
import co.kr.allpick.domain.seller.dto.SellerLoginResponseDto;
import co.kr.allpick.domain.seller.dto.SellerSignupRequestDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "SellerAuth", description = "판매자 인증 API")
public interface SellerAuthControllerDocs {

    @Operation(summary = "판매자 등록")
    ResponseEntity<ApiResponse<Void>> signup(SellerSignupRequestDto dto, Long memberId);

    @Operation(summary = "판매자 로그인")
    ResponseEntity<ApiResponse<SellerLoginResponseDto>> login(SellerLoginRequestDto dto);
}