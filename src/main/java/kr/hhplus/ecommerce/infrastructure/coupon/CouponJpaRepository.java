package kr.hhplus.ecommerce.infrastructure.coupon;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponJpaRepository extends JpaRepository<CouponJpaEntity, Long> {
    
    @Query("""
        SELECT c
        FROM coupons c
        WHERE c.issueStartTime <= :dateTime
        AND c.issueEndTime >= :dateTime
        AND c.issuedQuantity < c.maxQuantity
    """)
    List<CouponJpaEntity> findAvailableCoupons(LocalDateTime dateTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM coupons c WHERE c.id = :id")
    Optional<CouponJpaEntity> findByIdWithLock(@Param("id") long id);
}