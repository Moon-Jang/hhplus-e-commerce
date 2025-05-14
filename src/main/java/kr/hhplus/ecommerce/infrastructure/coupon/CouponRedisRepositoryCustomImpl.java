package kr.hhplus.ecommerce.infrastructure.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import static kr.hhplus.ecommerce.infrastructure.coupon.CouponRedisEntity.HASH_KEY_PREFIX;

@RequiredArgsConstructor
public class CouponRedisRepositoryCustomImpl implements CouponRedisRepositoryCustom {
    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean deductStock(long couponId) {
        long result = redisTemplate.opsForHash()
            .increment(generateKey(couponId), "quantity", -1);

        return result >= 0;
    }

    private String generateKey(long couponId) {
        return HASH_KEY_PREFIX + ':' + couponId;
    }
}
