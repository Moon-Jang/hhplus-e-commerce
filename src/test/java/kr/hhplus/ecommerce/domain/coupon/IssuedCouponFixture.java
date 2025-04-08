package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.common.FixtureReflectionUtils;
import kr.hhplus.ecommerce.common.TestFixture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class IssuedCouponFixture implements TestFixture<IssuedCoupon> {
    private Long id = 1L;
    private long userId = 1L;
    private LocalDate expiryDate = LocalDate.now().plusYears(1);
    private LocalDateTime usedAt = null;
    private Coupon coupon = new CouponFixture().create();

    @Override
    public IssuedCoupon create() {
        IssuedCoupon entity = new IssuedCoupon();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }
} 