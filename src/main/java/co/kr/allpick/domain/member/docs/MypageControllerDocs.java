package co.kr.allpick.domain.member.docs;

import co.kr.allpick.domain.member.dto.MemberResponseDto;
import co.kr.allpick.domain.member.dto.mypage.MemberUpdateRequestDto;
import co.kr.allpick.domain.member.dto.mypage.PasswordChangeRequestDto;
import co.kr.allpick.domain.order.dto.OrderResponseDto;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Mypage", description = "마이페이지 API")
public interface MypageControllerDocs {

    @Operation(summary = "내 정보 수정", description = "이름, 전화번호, 주소를 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "회원 정보 수정 성공",
                    "data": {
                        "id": 1,
                        "email": "test@test.com",
                        "name": "홍길동",
                        "phone": "01099998888",
                        "address": "서울시 강남구 역삼로 99",
                        "grade": "NORMAL"
                    }
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원 없음")
    })
    ResponseEntity<ApiResponse<MemberResponseDto>> updateMember(JwtUserInfoDto userInfo, MemberUpdateRequestDto request);

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 확인 후 새 비밀번호로 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "변경 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "비밀번호 변경 성공",
                    "data": null
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "현재 비밀번호 불일치"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원 없음")
    })
    ResponseEntity<ApiResponse<Void>> changePassword(JwtUserInfoDto userInfo, PasswordChangeRequestDto request);

    @Operation(summary = "회원 탈퇴", description = "회원을 소프트 삭제(deletedAt 설정)합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "탈퇴 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "회원 탈퇴 성공",
                    "data": null
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원 없음")
    })
    ResponseEntity<ApiResponse<Void>> deleteMember(JwtUserInfoDto userInfo);

    @Operation(summary = "내 주문 목록 조회", description = "로그인한 회원의 전체 주문 목록을 최신순으로 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "주문 목록 조회 성공",
                    "data": [
                        {
                            "orderId": 1,
                            "orderNumber": "ORD-20260415-000001",
                            "totalAmount": 50000,
                            "discountAmount": 0,
                            "memberCouponId": null,
                            "shippingFee": 3000,
                            "status": "PENDING",
                            "orderedAt": "2026-04-15T10:00:00",
                            "orderItems": [
                                {
                                    "orderItemId": 1,
                                    "productId": 10,
                                    "productName": "나이키 에어맥스",
                                    "productPrice": 47000,
                                    "quantity": 2,
                                    "totalPrice": 94000
                                }
                            ]
                        }
                    ]
                }
            """)))
    })
    ResponseEntity<ApiResponse<List<OrderResponseDto>>> getMyOrders(JwtUserInfoDto userInfo);
}
