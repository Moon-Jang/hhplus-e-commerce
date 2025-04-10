package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.common.FixtureReflectionUtils;
import kr.hhplus.ecommerce.common.TestFixture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class CouponFixture implements TestFixture<Coupon> {
    private Long id = 1L;
    private String name = "Test Coupon";
    private int discountAmount = 1000;
    private LocalDateTime issueStartTime = LocalDateTime.now();
    private LocalDateTime issueEndTime = LocalDateTime.now().plusHours(1);
    private int maxQuantity = 100;
    private int issuedQuantity = 0;
    private int expiryDays = 30;

    @Override
    public Coupon create() {
        Coupon entity = new Coupon();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }
}