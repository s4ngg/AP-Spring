package co.kr.allpick.domain.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.kr.allpick.domain.coupon.entity.Coupon;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCouponCode(String couponCode);
    boolean existsByCouponCode(String couponCode);
}