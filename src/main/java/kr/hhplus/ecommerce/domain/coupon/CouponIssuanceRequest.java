package kr.hhplus.ecommerce.domain.coupon;

public record CouponIssuanceRequest(
    long userId,
    long couponId,
    long requestTimeMillis
) {
}