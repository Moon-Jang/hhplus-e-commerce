package kr.hhplus.ecommerce.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface IssuedCouponRepository {
    Optional<IssuedCoupon> findById(long issuedCouponId);
    List<IssuedCoupon> findByUserId(long userId);
    IssuedCoupon save(IssuedCoupon issuedCoupon);
} 