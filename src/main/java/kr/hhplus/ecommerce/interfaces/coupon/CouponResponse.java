package kr.hhplus.ecommerce.interfaces.coupon;

import kr.hhplus.ecommerce.domain.coupon.CouponVo;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponVo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CouponResponse {
    public record CouponSummary(
            long id,
            String name,
            long discountAmount,
            LocalDateTime issueStartTime,
            LocalDateTime issueEndTime,
            int maxQuantity,
            int currentQuantity
    ) {
        public static CouponSummary from(CouponVo coupon) {
            return new CouponSummary(
                    coupon.id(),
                    coupon.name(),
                    coupon.discountAmount(),
                    coupon.issueStartTime(),
                    coupon.issueEndTime(),
                    coupon.maxQuantity(),
                    coupon.issuedQuantity()
            );
        }
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
        public static IssuedCouponDetails of(IssuedCouponVo issuedCoupon, CouponVo coupon) {
            return new IssuedCouponDetails(
                    issuedCoupon.id(),
                    issuedCoupon.userId(),
                    coupon.id(),
                    coupon.name(),
                    coupon.discountAmount(),
                    issuedCoupon.expiryDate(),
                    issuedCoupon.isUsed(),
                    issuedCoupon.usedAt(),
                    issuedCoupon.createdAt()
            );
        }
    }
} 