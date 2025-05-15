package kr.hhplus.ecommerce.infrastructure.coupon;

import java.util.List;
import java.util.Optional;

public interface IssuedCouponRedisRepositoryCustom {
    Optional<IssuedCouponRedisEntity> findByCouponIdAndUserId(long couponId, long userId);
    boolean existsByCouponIdAndUserId(long couponId, long userId);
    List<IssuedCouponRedisEntity> findByUserId(long userId);
}