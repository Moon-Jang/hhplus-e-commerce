package kr.hhplus.ecommerce.infrastructure.coupon;

import kr.hhplus.ecommerce.domain.coupon.CouponIssuanceRequest;
import kr.hhplus.ecommerce.domain.coupon.CouponIssuanceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponIssuanceRequestRepositoryImpl implements CouponIssuanceRequestRepository {
    public static final String COUPON_WAITING_QUEUE_KEY_PREFIX = "coupon_waiting_queue:";
    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(CouponIssuanceRequest couponIssuanceRequest) {
        redisTemplate.opsForZSet()
            .addIfAbsent(
                COUPON_WAITING_QUEUE_KEY_PREFIX + couponIssuanceRequest.couponId(),
                String.valueOf(couponIssuanceRequest.userId()),
                couponIssuanceRequest.requestTimeMillis()
            );
    }
}
