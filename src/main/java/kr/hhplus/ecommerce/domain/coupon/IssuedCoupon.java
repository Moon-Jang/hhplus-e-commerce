package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.domain.common.BaseEntity;
import kr.hhplus.ecommerce.domain.common.DomainException;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static kr.hhplus.ecommerce.domain.common.DomainStatus.ALREADY_USED_COUPON;
import static kr.hhplus.ecommerce.domain.common.DomainStatus.EXPIRED_COUPON;

@Getter
@Accessors(fluent = true)
public class IssuedCoupon extends BaseEntity {
    private Long id;
    private long userId;
    private long couponId;
    private LocalDate expiryDate;
    private LocalDateTime usedAt;

    public IssuedCoupon(Long id,
                        long userId,
                        long couponId,
                        LocalDate expiryDate,
                        LocalDateTime usedAt) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.expiryDate = expiryDate;
        this.usedAt = usedAt;
    }

    public IssuedCoupon(long userId,
                        long couponId,
                        LocalDate expiryDate) {
        this(
            null,
            userId,
            couponId,
            expiryDate,
            null
        );
    }

    public boolean isExpired() {
        LocalDate now = LocalDate.now();
        return now.isAfter(expiryDate);
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public void use() {
        if (isExpired()) {
            throw new DomainException(EXPIRED_COUPON);
        }

        if (isUsed()) {
            throw new DomainException(ALREADY_USED_COUPON);
        }

        this.usedAt = LocalDateTime.now();
    }

    public void validateUsable() {
        if (isExpired()) {
            throw new DomainException(EXPIRED_COUPON);
        }

        if (isUsed()) {
            throw new DomainException(ALREADY_USED_COUPON);
        }
    }
}