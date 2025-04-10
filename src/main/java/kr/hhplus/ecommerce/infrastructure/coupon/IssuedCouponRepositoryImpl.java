package kr.hhplus.ecommerce.infrastructure.coupon;

import kr.hhplus.ecommerce.domain.coupon.IssuedCoupon;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IssuedCouponRepositoryImpl implements IssuedCouponRepository {
    @Override
    public Optional<IssuedCoupon> findById(long issuedCouponId) {
        // TODO: Implement me
        return Optional.empty();
    }

    @Override
    public List<IssuedCoupon> findByUserId(long userId) {
        // TODO: Implement me
        return Collections.emptyList();
    }

    @Override
    public IssuedCoupon save(IssuedCoupon issuedCoupon) {
        // TODO: Implement me
        return null;
    }
}