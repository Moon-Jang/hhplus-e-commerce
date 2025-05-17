package kr.hhplus.ecommerce.domain.coupon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> findById(long id);
    Optional<Coupon> findByIdWithLock(long id);
    List<Coupon> findAvailableCoupons(LocalDateTime dateTime);
    Coupon save(Coupon coupon);
    boolean deductStock(long id);
}