package kr.hhplus.ecommerce.infrastructure.coupon;

import jakarta.persistence.Id;
import kr.hhplus.ecommerce.domain.coupon.Coupon;
import kr.hhplus.ecommerce.infrastructure.common.PersistenceEntity;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Getter
@Accessors(fluent = true)
@RedisHash(CouponRedisEntity.HASH_KEY_PREFIX)
public class CouponRedisEntity implements PersistenceEntity<Coupon> {
    public static final String HASH_KEY_PREFIX = "coupon";
    @Id
    private Long id;
    private String name;
    private int discountAmount;
    private LocalDateTime issueStartTime;
    private LocalDateTime issueEndTime;
    private int maxQuantity;
    private int quantity;
    private int expiryDays;

    public CouponRedisEntity(Long id,
                             String name,
                             int discountAmount,
                             LocalDateTime issueStartTime,
                             LocalDateTime issueEndTime,
                             int maxQuantity,
                             int quantity,
                             int expiryDays) {
        this.id = id;
        this.name = name;
        this.discountAmount = discountAmount;
        this.issueStartTime = issueStartTime;
        this.issueEndTime = issueEndTime;
        this.maxQuantity = maxQuantity;
        this.quantity = quantity;
        this.expiryDays = expiryDays;
    }

    public static CouponRedisEntity from(Coupon coupon) {
        return new CouponRedisEntity(
            coupon.id(),
            coupon.name(),
            coupon.discountAmount(),
            coupon.issueStartTime(),
            coupon.issueEndTime(),
            coupon.maxQuantity(),
            coupon.maxQuantity() - coupon.issuedQuantity(),
            coupon.expiryDays()
        );
    }

    @Override
    public Coupon toDomain() {
        return new Coupon(
            id,
            name,
            discountAmount,
            issueStartTime,
            issueEndTime,
            maxQuantity,
            Math.max(maxQuantity - quantity, 0),
            expiryDays
        );
    }
}
