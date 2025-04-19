package kr.hhplus.ecommerce.infrastructure.coupon;

import kr.hhplus.ecommerce.domain.coupon.IssuedCoupon;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IssuedCouponRepositoryImpl implements IssuedCouponRepository {
    private final IssuedCouponJpaRepository issuedCouponJpaRepository;
    
    @Override
    public Optional<IssuedCoupon> findById(long issuedCouponId) {
        return issuedCouponJpaRepository.findById(issuedCouponId);
    }

    @Override
    public List<IssuedCoupon> findByUserId(long userId) {
        return issuedCouponJpaRepository.findByUserId(userId);
    }

    @Override
    public IssuedCoupon save(IssuedCoupon issuedCoupon) {
        return issuedCouponJpaRepository.save(issuedCoupon);
    }
}