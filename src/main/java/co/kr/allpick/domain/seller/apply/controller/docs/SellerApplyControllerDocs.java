package co.kr.allpick.domain.seller.apply.controller.docs;

import co.kr.allpick.domain.seller.apply.dto.SellerApplyRequestDto;
import co.kr.allpick.domain.seller.apply.
dto.SellerApplyStatusResponseDto;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "SellerApply", description = "판매자 신청 API")
public interface SellerApplyControllerDocs {

    @Operation(summary = "판매자 신청")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "신청 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 신청된 판매자")
    })
    ResponseEntity<ApiResponse<Void>> apply(
            @RequestBody @Valid SellerApplyRequestDto dto,
            @AuthenticationPrincipal JwtUserInfoDto userInfo);

    @Operation(summary = "판매자 신청 상태 조회")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "신청 내역 없음")
    })
    ResponseEntity<ApiResponse<SellerApplyStatusResponseDto>> getApplyStatus(
            @AuthenticationPrincipal JwtUserInfoDto userInfo);
}