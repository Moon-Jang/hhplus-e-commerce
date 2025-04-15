package kr.hhplus.ecommerce.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.common.entity.BaseEntity;
import kr.hhplus.ecommerce.common.exception.DomainException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static kr.hhplus.ecommerce.common.support.DomainStatus.ALREADY_USED_COUPON;
import static kr.hhplus.ecommerce.common.support.DomainStatus.EXPIRED_COUPON;

@Entity(name = "issued_coupons")
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class IssuedCoupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long userId;
    private LocalDate expiryDate;
    private LocalDateTime usedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    public IssuedCoupon(long userId,
                        LocalDate expiryDate,
                        Coupon coupon) {
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.coupon = coupon;
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

    public int discountAmount() {
        return coupon.discountAmount();
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