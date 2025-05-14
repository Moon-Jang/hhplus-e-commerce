package kr.hhplus.ecommerce.common.constant;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CacheNames {
    public static final String TOP_SELLING_PRODUCTS = "PRODUCT::TOP_SELLING";
    private static final long TOP_SELLING_PRODUCTS_EXPIRATION_MIN = 2 * 24 * 60L;
    public static final String PRODUCT_DETAILS = "PRODUCT::DETAILS";
    private static final long PRODUCT_DETAILS_EXPIRATION_MIN = 60L;
    public static final String COUPON_DETAILS = "COUPON::DETAILS";
    private static final long COUPON_DETAILS_EXPIRATION_MIN = 60L;

    public static List<CacheName> getAll() {
        return List.of(
            new CacheName(TOP_SELLING_PRODUCTS, TOP_SELLING_PRODUCTS_EXPIRATION_MIN, TimeUnit.MINUTES),
            new CacheName(PRODUCT_DETAILS, PRODUCT_DETAILS_EXPIRATION_MIN, TimeUnit.MINUTES),
            new CacheName(COUPON_DETAILS, COUPON_DETAILS_EXPIRATION_MIN, TimeUnit.MINUTES)
        );
    }

    public record CacheName(
        String name,
        long expirationTime,
        TimeUnit timeUnit
    ) {
    }
}