package kr.hhplus.ecommerce.infrastructure.coupon;

import kr.hhplus.ecommerce.domain.coupon.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCoupon, Long> {
    List<IssuedCoupon> findByUserId(long userId);
} 