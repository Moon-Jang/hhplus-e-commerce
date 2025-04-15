package kr.hhplus.ecommerce.infrastructure.coupon;

import kr.hhplus.ecommerce.domain.coupon.Coupon;
import kr.hhplus.ecommerce.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    @Override
    public Optional<Coupon> findById(long couponId) {
        // TODO: Implement with actual DB access
        return Optional.empty();
    }

    @Override
    public List<Coupon> findAvailableCoupons(LocalDateTime dateTime) {
        // TODO: Implement with actual DB access
        return Collections.emptyList();
    }

    @Override
    public Coupon save(Coupon coupon) {
        // TODO: Implement with actual DB access
        return coupon;
    }
} 