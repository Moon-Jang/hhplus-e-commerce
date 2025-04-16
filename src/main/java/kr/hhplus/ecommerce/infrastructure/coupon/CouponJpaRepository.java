package kr.hhplus.ecommerce.infrastructure.coupon;

import kr.hhplus.ecommerce.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
    
    @Query("""
        SELECT c
        FROM coupons c
        WHERE c.issueStartTime <= :dateTime
        AND c.issueEndTime >= :dateTime
        AND c.issuedQuantity < c.maxQuantity
    """)
    List<Coupon> findAvailableCoupons(LocalDateTime dateTime);
} 