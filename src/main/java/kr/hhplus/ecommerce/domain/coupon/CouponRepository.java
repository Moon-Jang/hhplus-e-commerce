package kr.hhplus.ecommerce.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> findById(long couponId);
    List<Coupon> findAvailableCoupons();
    Coupon save(Coupon coupon);
} 