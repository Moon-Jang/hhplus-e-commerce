package kr.hhplus.ecommerce.infrastructure.coupon;

public interface CouponRedisRepositoryCustom {
    boolean deductStock(long couponId);
}
