package co.kr.allpick.domain.coupon.controller.docs;

import co.kr.allpick.domain.coupon.dto.CouponRegisterRequestDto;
import co.kr.allpick.domain.coupon.dto.CouponResponseDto;
import co.kr.allpick.domain.coupon.dto.MemberCouponResponseDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Coupon", description = "쿠폰 API")
public interface CouponControllerDocs {

    @Operation(summary = "쿠폰 등록", description = "관리자가 쿠폰을 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "쿠폰 등록 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "쿠폰이 등록되었습니다.",
                    "data": {
                        "couponId": 1,
                        "couponCode": "WELCOME2026",
                        "discountType": "PERCENT",
                        "discountValue": 10,
                        "minOrderAmount": 10000,
                        "maxDiscount": 5000,
                        "expiredAt": "2026-12-31T23:59:59"
                    }
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 요청",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "이미 존재하는 쿠폰 코드입니다.",
                    "data": null
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 할인값",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "PERCENT 할인값은 1~100 사이여야 합니다.",
                    "data": null
                }
            """)))
    })
    ResponseEntity<ApiResponse<CouponResponseDto>> registerCoupon(
            @RequestBody @Valid CouponRegisterRequestDto request);

    @Operation(summary = "쿠폰 발급", description = "회원에게 쿠폰을 발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "쿠폰 발급 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "쿠폰이 발급되었습니다.",
                    "data": {
                        "memberCouponId": 1,
                        "memberId": 1,
                        "coupon": {
                            "couponId": 1,
                            "couponCode": "WELCOME2026",
                            "discountType": "PERCENT",
                            "discountValue": 10,
                            "minOrderAmount": 10000,
                            "maxDiscount": 5000,
                            "expiredAt": "2026-12-31T23:59:59"
                        },
                        "isUsed": false,
                        "usedAt": null
                    }
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미 보유한 쿠폰",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "이미 보유한 쿠폰입니다.",
                    "data": null
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "만료된 쿠폰",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "만료된 쿠폰입니다.",
                    "data": null
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원 또는 쿠폰 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "회원을 찾을 수 없습니다.",
                    "data": null
                }
            """)))
    })
    ResponseEntity<ApiResponse<MemberCouponResponseDto>> issueCoupon(
            @PathVariable("memberId") Long memberId,
            @PathVariable("couponId") Long couponId);

    @Operation(summary = "회원 보유 쿠폰 목록 조회", description = "회원이 보유한 전체 쿠폰 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "보유 쿠폰 목록 조회 성공.",
                    "data": [
                        {
                            "memberCouponId": 1,
                            "memberId": 1,
                            "coupon": {
                                "couponId": 1,
                                "couponCode": "WELCOME2026",
                                "discountType": "PERCENT",
                                "discountValue": 10,
                                "minOrderAmount": 10000,
                                "maxDiscount": 5000,
                                "expiredAt": "2026-12-31T23:59:59"
                            },
                            "isUsed": false,
                            "usedAt": null
                        }
                    ]
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "회원을 찾을 수 없습니다.",
                    "data": null
                }
            """)))
    })
    ResponseEntity<ApiResponse<List<MemberCouponResponseDto>>> getMemberCoupons(
            @PathVariable("memberId") Long memberId);

    @Operation(summary = "회원 미사용 쿠폰 목록 조회", description = "회원이 보유한 미사용 쿠폰 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "미사용 쿠폰 목록 조회 성공.",
                    "data": [
                        {
                            "memberCouponId": 1,
                            "memberId": 1,
                            "coupon": {
                                "couponId": 1,
                                "couponCode": "WELCOME2026",
                                "discountType": "PERCENT",
                                "discountValue": 10,
                                "minOrderAmount": 10000,
                                "maxDiscount": 5000,
                                "expiredAt": "2026-12-31T23:59:59"
                            },
                            "isUsed": false,
                            "usedAt": null
                        }
                    ]
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "회원을 찾을 수 없습니다.",
                    "data": null
                }
            """)))
    })
    ResponseEntity<ApiResponse<List<MemberCouponResponseDto>>> getUnusedCoupons(
            @PathVariable("memberId") Long memberId);

    @Operation(summary = "쿠폰 코드 조회", description = "쿠폰 코드로 쿠폰 정보를 조회합니다. 만료된 쿠폰은 조회되지 않습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "쿠폰 조회 성공.",
                    "data": {
                        "couponId": 1,
                        "couponCode": "WELCOME2026",
                        "discountType": "PERCENT",
                        "discountValue": 10,
                        "minOrderAmount": 10000,
                        "maxDiscount": 5000,
                        "expiredAt": "2026-12-31T23:59:59"
                    }
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "만료된 쿠폰",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "만료된 쿠폰입니다.",
                    "data": null
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "쿠폰 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "쿠폰을 찾을 수 없습니다.",
                    "data": null
                }
            """)))
    })
    ResponseEntity<ApiResponse<CouponResponseDto>> getCouponByCode(
            @RequestParam("couponCode") String couponCode);
}