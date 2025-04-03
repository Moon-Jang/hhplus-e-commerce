package kr.hhplus.ecommerce.controller.response;

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

    public record IssuedCoupon(
            long id,
            long userId,
            long couponId,
            String couponName,
            long discountAmount,
            LocalDateTime expiryTime,
            boolean isUsed,
            LocalDateTime usedAt,
            LocalDateTime createdAt
    ) {
    }
} 