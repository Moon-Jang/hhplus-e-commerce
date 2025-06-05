package kr.hhplus.ecommerce.infrastructure.coupon;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCouponJpaEntity, Long> {
    List<IssuedCouponJpaEntity> findByUserId(long userId);
    List<IssuedCouponJpaEntity> findAllByUserIdAndUsedAtIsNull(long userId);
    boolean existsByCouponIdAndUserId(long couponId, long userId);
} 