package kr.hhplus.ecommerce.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface IssuedCouponRepository {
    Optional<IssuedCoupon> findById(long issuedCouponId);
    Optional<IssuedCoupon> findByCouponIdAndUserId(long couponId, long userId);
    List<IssuedCoupon> findByUserId(long userId);
    IssuedCoupon save(IssuedCoupon issuedCoupon);
    boolean isAlreadyIssued(long couponId, long userId);
} 