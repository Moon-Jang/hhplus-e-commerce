package kr.hhplus.ecommerce.infrastructure.coupon;

import kr.hhplus.ecommerce.domain.coupon.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCoupon, Long> {
    List<IssuedCoupon> findByUserId(long userId);
    boolean existsByCouponIdAndUserId(long couponId, long userId);
} 