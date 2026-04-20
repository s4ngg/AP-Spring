package co.kr.allpick.domain.order.repository;

import co.kr.allpick.domain.order.entity.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {
    List<MemberCoupon> findByMemberId(Long memberId);
    List<MemberCoupon> findByMemberIdAndIsUsed(Long memberId, boolean isUsed);
    Optional<MemberCoupon> findByMemberIdAndCoupon_CouponId(Long memberId, Long couponId);
    boolean existsByMemberIdAndCoupon_CouponId(Long memberId, Long couponId);
}