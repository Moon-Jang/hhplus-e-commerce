package kr.hhplus.ecommerce.infrastructure.common;

import kr.hhplus.ecommerce.domain.common.CacheTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCacheTemplate implements CacheTemplate {
    private final StringRedisTemplate redisTemplate;

    @Async
    @Override
    public void evictAsync(String key) {
        redisTemplate.delete(key);
    }
}