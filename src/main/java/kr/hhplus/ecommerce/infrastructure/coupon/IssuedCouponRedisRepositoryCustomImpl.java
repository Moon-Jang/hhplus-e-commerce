package kr.hhplus.ecommerce.infrastructure.coupon;

import kr.hhplus.ecommerce.common.support.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.ecommerce.infrastructure.coupon.IssuedCouponRedisEntity.HASH_KEY_PREFIX;

@RequiredArgsConstructor
public class IssuedCouponRedisRepositoryCustomImpl implements IssuedCouponRedisRepositoryCustom {
    private final StringRedisTemplate redisTemplate;

    @Override
    public Optional<IssuedCouponRedisEntity> findByCouponIdAndUserId(long couponId, long userId) {
        if (!existsByCouponIdAndUserId(couponId, userId)) {
            return Optional.empty();
        }

        IssuedCouponRedisEntity result = JsonUtils.parse(
            JsonUtils.stringify(redisTemplate.opsForHash()
                .entries(IssuedCouponRedisEntity.fullKey(couponId, userId))),
            IssuedCouponRedisEntity.class
        );

        return Optional.of(result);
    }

    @Override
    public boolean existsByCouponIdAndUserId(long couponId, long userId) {
        return redisTemplate.hasKey(
            IssuedCouponRedisEntity.fullKey(couponId, userId)
        );
    }

    @Override
    public List<IssuedCouponRedisEntity> findByUserId(long userId) {
        return redisTemplate.opsForSet().members(IssuedCouponRedisEntity.listByUserIdKey(userId))
            .stream()
            .map(key -> redisTemplate.opsForHash().entries(HASH_KEY_PREFIX + ":" + key))
            .map(map -> JsonUtils.parse(
                JsonUtils.stringify(map),
                IssuedCouponRedisEntity.class
            ))
            .toList();
    }
}