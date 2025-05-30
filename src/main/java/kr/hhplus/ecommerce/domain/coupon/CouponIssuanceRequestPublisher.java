package kr.hhplus.ecommerce.domain.coupon;

public interface CouponIssuanceRequestPublisher {
    void publish(CouponIssuanceRequest request);
}
