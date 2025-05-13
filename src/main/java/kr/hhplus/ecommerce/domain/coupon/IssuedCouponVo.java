package kr.hhplus.ecommerce.domain.coupon;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record IssuedCouponVo(
    long id,
    long userId,
    long couponId,
    LocalDate expiryDate,
    boolean isUsed,
    LocalDateTime usedAt,
    LocalDateTime createdAt
) {
    public static IssuedCouponVo from(IssuedCoupon issuedCoupon) {
        return new IssuedCouponVo(
            issuedCoupon.id(),
            issuedCoupon.userId(),
            issuedCoupon.couponId(),
            issuedCoupon.expiryDate(),
            issuedCoupon.isUsed(),
            issuedCoupon.usedAt(),
            issuedCoupon.createdAt()
        );
    }
} 