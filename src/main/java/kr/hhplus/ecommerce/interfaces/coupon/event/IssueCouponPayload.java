package kr.hhplus.ecommerce.interfaces.coupon.event;

public class IssueCouponPayload {
    public record Issue(
            Long userId,
            Long couponId
    ) {
    }
}
