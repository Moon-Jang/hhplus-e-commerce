package kr.hhplus.ecommerce.domain.common;

public interface CacheTemplate {
    void evictAsync(String key);
}