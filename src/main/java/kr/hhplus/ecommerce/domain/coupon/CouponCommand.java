package kr.hhplus.ecommerce.domain.coupon;

public class CouponCommand {
    public record Issue(
        long userId,
        long couponId
    ) {
    }

    public record RequestIssuance(
        long userId,
        long couponId,
        long requestTimeMillis
    ) {
    }
} 