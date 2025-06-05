package kr.hhplus.ecommerce.domain.coupon;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record IssuedCouponWithDetailsVo(
    long id,
    long userId,
    long couponId,
    String couponName,
    long discountAmount,
    LocalDate expiryDate,
    boolean isUsed,
    LocalDateTime usedAt,
    LocalDateTime createdAt
) {
    public static IssuedCouponWithDetailsVo of(IssuedCouponVo issuedCoupon, CouponVo coupon) {
        return new IssuedCouponWithDetailsVo(
            issuedCoupon.id(),
            issuedCoupon.userId(),
            issuedCoupon.couponId(),
            coupon.name(),
            coupon.discountAmount(),
            issuedCoupon.expiryDate(),
            issuedCoupon.isUsed(),
            issuedCoupon.usedAt(),
            issuedCoupon.createdAt()
        );
    }
} 