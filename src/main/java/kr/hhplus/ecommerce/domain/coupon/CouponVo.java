package kr.hhplus.ecommerce.domain.coupon;

import java.time.LocalDateTime;

public record CouponVo(
    long id,
    String name,
    int discountAmount,
    LocalDateTime issueStartTime,
    LocalDateTime issueEndTime,
    int maxQuantity,
    int issuedQuantity,
    int expiryDays
) {
    public static CouponVo from(Coupon coupon) {
        return new CouponVo(
            coupon.id(),
            coupon.name(),
            coupon.discountAmount(),
            coupon.issueStartTime(),
            coupon.issueEndTime(),
            coupon.maxQuantity(),
            coupon.issuedQuantity(),
            coupon.expiryDays()
        );
    }
} 