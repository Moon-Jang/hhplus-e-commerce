package kr.hhplus.ecommerce.infrastructure.coupon;

import kr.hhplus.ecommerce.domain.coupon.Coupon;
import kr.hhplus.ecommerce.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {
    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Optional<Coupon> findById(long id) {
        return couponJpaRepository.findById(id);
    }

    @Override
    public Optional<Coupon> findByIdWithLock(long id) {
        return couponJpaRepository.findByIdWithLock(id);
    }

    @Override
    public List<Coupon> findAvailableCoupons(LocalDateTime dateTime) {
        return couponJpaRepository.findAvailableCoupons(dateTime);
    }

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }
} 