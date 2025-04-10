package kr.hhplus.ecommerce.interfaces.coupon;

import kr.hhplus.ecommerce.domain.coupon.IssuedCouponVo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CouponResponse {
    public record Coupon(
            long id,
            String name,
            long discountAmount,
            LocalDateTime issueStartTime,
            LocalDateTime issueEndTime,
            int maxQuantity,
            int currentQuantity
    ) {
    }

    public record IssuedCouponDetails(
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
        public static IssuedCouponDetails from(IssuedCouponVo issuedCoupon) {
            return new IssuedCouponDetails(
                    issuedCoupon.id(),
                    issuedCoupon.userId(),
                    issuedCoupon.coupon().id(),
                    issuedCoupon.coupon().name(),
                    issuedCoupon.coupon().discountAmount(),
                    issuedCoupon.expiryDate(),
                    issuedCoupon.isUsed(),
                    issuedCoupon.usedAt(),
                    issuedCoupon.createdAt()
            );
        }
    }
} 