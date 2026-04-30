package co.kr.allpick.domain.seller.apply.controller;
import co.kr.allpick.domain.seller.apply.controller.docs.SellerApplyControllerDocs;
import co.kr.allpick.domain.seller.apply.dto.SellerApplyRequestDto;
import co.kr.allpick.domain.seller.apply.dto.SellerApplyStatusResponseDto;
import co.kr.allpick.domain.seller.apply.service.SellerApplyService;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerApplyController implements SellerApplyControllerDocs {

    private final SellerApplyService sellerApplyService;

    @Override
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<Void>> apply(
            @RequestBody SellerApplyRequestDto dto,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        sellerApplyService.apply(dto, userInfo.getMemberId());
        return ApiResponse.success("판매자 신청이 완료되었습니다.");
    }

    @Override
    @GetMapping("/apply/status")
    public ResponseEntity<ApiResponse<SellerApplyStatusResponseDto>> getApplyStatus(
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        return ApiResponse.success("조회 성공",
                sellerApplyService.getApplyStatus(userInfo.getMemberId()));
    }
}