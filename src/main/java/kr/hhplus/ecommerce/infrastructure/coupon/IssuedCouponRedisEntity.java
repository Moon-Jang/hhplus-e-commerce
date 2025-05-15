package kr.hhplus.ecommerce.infrastructure.coupon;

import kr.hhplus.ecommerce.domain.coupon.IssuedCoupon;
import kr.hhplus.ecommerce.infrastructure.common.PersistenceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static kr.hhplus.ecommerce.infrastructure.coupon.IssuedCouponRedisEntity.HASH_KEY_PREFIX;


@RedisHash(HASH_KEY_PREFIX)
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class IssuedCouponRedisEntity implements PersistenceEntity<IssuedCoupon> {
    public static final String HASH_KEY_PREFIX = "issued_coupon";
    public static final String KEY_PATTERN = "coupon-%s-user-%s";
    public static final String LIST_BY_USER_ID_KEY_PATTERN = "issued_coupon:userId:%s";
    @Id
    private String key;
    private long rdbId;
    @Indexed
    private long userId;
    private long couponId;
    private LocalDate expiryDate;
    private LocalDateTime usedAt;

    public IssuedCouponRedisEntity(String key,
                                   long rdbId,
                                   long userId,
                                   long couponId,
                                   LocalDate expiryDate,
                                   LocalDateTime usedAt) {
        this.key = key;
        this.rdbId = rdbId;
        this.userId = userId;
        this.couponId = couponId;
        this.expiryDate = expiryDate;
        this.usedAt = usedAt;
    }

    public static IssuedCouponRedisEntity from(IssuedCoupon issuedCoupon) {
        return new IssuedCouponRedisEntity(
            generateKey(issuedCoupon.couponId(), issuedCoupon.userId()),
            issuedCoupon.id(),
            issuedCoupon.userId(),
            issuedCoupon.couponId(),
            issuedCoupon.expiryDate(),
            issuedCoupon.usedAt()
        );
    }

    public static String generateKey(long couponId, long userId) {
        return KEY_PATTERN.formatted(couponId, userId);
    }

    public static String fullKey(long couponId, long userId) {
        return HASH_KEY_PREFIX + ":" + generateKey(couponId, userId);
    }

    public static String listByUserIdKey(long userId) {
        return LIST_BY_USER_ID_KEY_PATTERN.formatted(userId);
    }

    @Override
    public IssuedCoupon toDomain() {
        return new IssuedCoupon(
            rdbId,
            userId,
            couponId,
            expiryDate,
            usedAt
        );
    }
}
