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
    private final IssuedCouponRedisRepository issuedCouponRedisRepository;

    @Override
    public Optional<IssuedCoupon> findById(long issuedCouponId) {
        return issuedCouponJpaRepository.findById(issuedCouponId)
            .map(IssuedCouponJpaEntity::toDomain);
    }

    @Override
    public Optional<IssuedCoupon> findByCouponIdAndUserId(long couponId, long userId) {
        return issuedCouponRedisRepository.findByCouponIdAndUserId(couponId, userId)
            .map(IssuedCouponRedisEntity::toDomain);
    }

    @Override
    public List<IssuedCoupon> findByUserId(long userId) {
        return issuedCouponRedisRepository.findByUserId(userId)
            .stream()
            .map(IssuedCouponRedisEntity::toDomain)
            .toList();
    }

    @Override
    public List<IssuedCoupon> findAllActiveByUserId(long userId) {
        return issuedCouponJpaRepository.findAllByUserIdAndUsedAtIsNull(userId)
            .stream()
            .map(IssuedCouponJpaEntity::toDomain)
            .toList();
    }

    @Override
    public IssuedCoupon save(IssuedCoupon issuedCoupon) {
        IssuedCouponRedisEntity redisEntity;

        if (issuedCoupon.id() == null || issuedCoupon.isUsed()) {
            IssuedCoupon saved = issuedCouponJpaRepository.save(IssuedCouponJpaEntity.from(issuedCoupon))
                .toDomain();
            redisEntity = IssuedCouponRedisEntity.from(saved);
        } else {
            redisEntity = IssuedCouponRedisEntity.from(issuedCoupon);
        }

        return issuedCouponRedisRepository.save(redisEntity)
            .toDomain();
    }

    @Override
    public boolean isAlreadyIssued(long couponId, long userId) {
        return issuedCouponRedisRepository.existsByCouponIdAndUserId(couponId, userId);
    }
}