package kr.hhplus.ecommerce.interfaces.coupon.api;

import kr.hhplus.ecommerce.domain.coupon.CouponVo;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponVo;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponWithDetailsVo;

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

    public record IssuedCouponSummary(
            long id,
            long userId,
            long couponId,
            LocalDate expiryDate,
            boolean isUsed,
            LocalDateTime usedAt,
            LocalDateTime createdAt
    ) {
        public static IssuedCouponSummary from(IssuedCouponVo issuedCoupon) {
            return new IssuedCouponSummary(
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

        public static IssuedCouponDetails fromWithDetails(IssuedCouponWithDetailsVo issuedCouponWithDetails) {
            return new IssuedCouponDetails(
                    issuedCouponWithDetails.id(),
                    issuedCouponWithDetails.userId(),
                    issuedCouponWithDetails.couponId(),
                    issuedCouponWithDetails.couponName(),
                    issuedCouponWithDetails.discountAmount(),
                    issuedCouponWithDetails.expiryDate(),
                    issuedCouponWithDetails.isUsed(),
                    issuedCouponWithDetails.usedAt(),
                    issuedCouponWithDetails.createdAt()
            );
        }
    }
} 