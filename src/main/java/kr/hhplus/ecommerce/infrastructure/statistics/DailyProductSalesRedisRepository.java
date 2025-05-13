package kr.hhplus.ecommerce.infrastructure.statistics;

import kr.hhplus.ecommerce.domain.statistics.DailyProductSales;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.time.format.DateTimeFormatter.ofPattern;

@Repository
@RequiredArgsConstructor
public class DailyProductSalesRedisRepository  {
    private static final String KEY_PREFIX = "product_sales:daily:";
    private final StringRedisTemplate redisTemplate;

    public void saveDelta(DailyProductSales delta) {
        LocalDate aggregationDate = delta.aggregationDate();
        String key = KEY_PREFIX + aggregationDate.format(ofPattern("yyyyMMdd"));

        redisTemplate.opsForZSet()
            .incrementScore(
                key,
                String.valueOf(delta.productId()),
                delta.countDelta()
            );
        redisTemplate.expireAt(
            key,
            aggregationDate.plusDays(3)
                .atStartOfDay()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant()
        );
    }

    public List<AggregationResult> aggregateRanking(LocalDate from, LocalDate to, int limit) {
        ZSetOperations<String, String> operations = redisTemplate.opsForZSet();
        Map<Long, Integer> result = new HashMap<>();

        IntStream.range(0, from.until(to).getDays() + 1)
            .mapToObj(i -> KEY_PREFIX + from.plusDays(i).format(ofPattern("yyyyMMdd")))
            .map(key -> operations.reverseRangeWithScores(key, 0, limit * 2L))
            .flatMap(Collection::stream)
            .forEach(score -> {
                Long productId = Long.valueOf(score.getValue());
                double scoreValue = score.getScore();
                result.merge(productId, (int) scoreValue, Integer::sum);
            });

        return result.entrySet().stream()
            .map(entry -> new AggregationResult(entry.getKey(), entry.getValue()))
            .toList();
    }

    public record AggregationResult(
        Long productId,
        int salesCount
    ) {
    }
}