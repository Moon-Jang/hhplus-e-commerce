package kr.hhplus.ecommerce.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.domain.common.BaseEntity;
import kr.hhplus.ecommerce.domain.common.DomainException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static kr.hhplus.ecommerce.domain.common.DomainStatus.ALREADY_USED_COUPON;
import static kr.hhplus.ecommerce.domain.common.DomainStatus.EXPIRED_COUPON;

@Entity(name = "issued_coupons")
@Table(indexes = {
    @Index(name = "idx_issued_coupon_user_id", columnList = "userId"),
    @Index(name = "idx_issued_coupon_coupon_id", columnList = "coupon_id")
})
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class IssuedCoupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long userId;
    private long couponId;
    private LocalDate expiryDate;
    private LocalDateTime usedAt;

    public IssuedCoupon(long userId,
                        long couponId,
                        LocalDate expiryDate) {
        this.userId = userId;
        this.couponId = couponId;
        this.expiryDate = expiryDate;
        this.usedAt = null;
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