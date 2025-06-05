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
    private final CouponRedisRepository couponRedisRepository;

    @Override
    public Optional<Coupon> findById(long id) {
        return couponRedisRepository.findById(id)
            .map(CouponRedisEntity::toDomain)
            .or(() -> couponJpaRepository.findById(id)
                .map(CouponJpaEntity::toDomain));
    }

    @Override
    public Optional<Coupon> findByIdWithLock(long id) {
        return couponJpaRepository.findByIdWithLock(id)
            .map(CouponJpaEntity::toDomain);
    }

    @Override
    public List<Coupon> findAvailableCoupons(LocalDateTime dateTime) {
        return couponJpaRepository.findAvailableCoupons(dateTime)
            .stream()
            .map(CouponJpaEntity::toDomain)
            .toList();
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponJpaEntity jpaEntity = CouponJpaEntity.from(coupon);
        CouponRedisEntity redisEntity;

        if (jpaEntity.id() == null) {
             Coupon saved = couponJpaRepository.save(jpaEntity)
                .toDomain();
            redisEntity = CouponRedisEntity.from(saved);
        } else {
            redisEntity = CouponRedisEntity.from(coupon);
        }

        return couponRedisRepository.save(redisEntity)
            .toDomain();
    }

    @Override
    public boolean deductStock(long id) {
        return couponRedisRepository.deductStock(id);
    }

    @Override
    public List<Coupon> findAllByIds(List<Long> ids) {
        return couponJpaRepository.findAllByIdIn(ids)
            .stream()
            .map(CouponJpaEntity::toDomain)
            .toList();
    }
} 