package kr.hhplus.ecommerce.domain.coupon;

import java.util.List;

public interface CouponIssuanceRequestRepository {
    void save(CouponIssuanceRequest couponIssuanceRequest);
    List<CouponIssuanceRequest> findAllWaitingList(int size);
}