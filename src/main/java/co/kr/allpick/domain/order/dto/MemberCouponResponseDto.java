package co.kr.allpick.domain.order.dto;

import co.kr.allpick.domain.order.entity.MemberCoupon;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "회원 쿠폰 응답 DTO")
public class MemberCouponResponseDto {

    @Schema(description = "회원 쿠폰 ID", example = "1")
    private Long memberCouponId;

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "쿠폰 정보")
    private CouponResponseDto coupon;

    @Schema(description = "사용 여부", example = "false")
    private boolean isUsed;

    @Schema(description = "사용 일시")
    private LocalDateTime usedAt;

    public static MemberCouponResponseDto from(MemberCoupon memberCoupon) {
        return MemberCouponResponseDto.builder()
                .memberCouponId(memberCoupon.getMemberCouponId())
                .memberId(memberCoupon.getMemberId())
                .coupon(CouponResponseDto.from(memberCoupon.getCoupon()))
                .isUsed(memberCoupon.isUsed())
                .usedAt(memberCoupon.getUsedAt())
                .build();
    }
}