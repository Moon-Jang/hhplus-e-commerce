package kr.hhplus.ecommerce.domain.coupon;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record IssuedCouponVo(
    long id,
    long userId,
    CouponVo coupon,
    LocalDate expiryDate,
    boolean isUsed,
    LocalDateTime usedAt,
    LocalDateTime createdAt
) {
    public static IssuedCouponVo from(IssuedCoupon issuedCoupon) {
        return new IssuedCouponVo(
            issuedCoupon.id(),
            issuedCoupon.userId(),
            CouponVo.from(issuedCoupon.coupon()),
            issuedCoupon.expiryDate(),
            issuedCoupon.isUsed(),
            issuedCoupon.usedAt(),
            issuedCoupon.createdAt()
        );
    }
} 