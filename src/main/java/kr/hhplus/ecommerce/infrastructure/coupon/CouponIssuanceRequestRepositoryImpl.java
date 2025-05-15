package kr.hhplus.ecommerce.infrastructure.coupon;

import kr.hhplus.ecommerce.domain.coupon.CouponIssuanceRequest;
import kr.hhplus.ecommerce.domain.coupon.CouponIssuanceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public List<CouponIssuanceRequest> findAllWaitingList(int size) {
        Set<String> allKeys = new HashSet<>();
        ScanOptions scanOptions = ScanOptions.scanOptions()
            .match(COUPON_WAITING_QUEUE_KEY_PREFIX + "*")
            .count(1000)
            .type(DataType.ZSET)
            .build();

        try (Cursor<String> cur = redisTemplate.scan(scanOptions)) {
            cur.forEachRemaining(allKeys::add);
        }

        List<CouponIssuanceRequest> result = new ArrayList<>();

        for (String key : allKeys) {
            if (result.size() >= size) {
                break;
            }

            var requests = redisTemplate.opsForZSet().popMin(key, size)
                .stream()
                .map(tuple -> new CouponIssuanceRequest(
                    Long.parseLong(tuple.getValue()),
                    Long.parseLong(key.split(":")[1]),
                    tuple.getScore().longValue()
                ))
                .toList();

            result.addAll(requests);
        }

        return result.stream()
            .limit(size)
            .toList();
    }
}
