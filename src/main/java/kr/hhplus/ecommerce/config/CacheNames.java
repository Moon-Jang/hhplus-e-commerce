package kr.hhplus.ecommerce.config;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CacheNames {

    public static List<CacheName> getAll() {
        return List.of(
        );
    }

    public record CacheName(
        String name,
        long expirationTime,
        TimeUnit timeUnit
    ) {
    }
}