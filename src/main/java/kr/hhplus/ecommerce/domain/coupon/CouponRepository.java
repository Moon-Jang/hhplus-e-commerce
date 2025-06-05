package kr.hhplus.ecommerce.domain.coupon;

import com.querydsl.core.Fetchable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> findById(long id);
    Optional<Coupon> findByIdWithLock(long id);
    List<Coupon> findAvailableCoupons(LocalDateTime dateTime);
    Coupon save(Coupon coupon);
    boolean deductStock(long id);
    List<Coupon> findAllByIds(List<Long> ids);
}