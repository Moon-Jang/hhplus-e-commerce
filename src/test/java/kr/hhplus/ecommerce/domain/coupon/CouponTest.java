package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static kr.hhplus.ecommerce.common.support.DomainStatus.COUPON_EXHAUSTED;
import static kr.hhplus.ecommerce.common.support.DomainStatus.COUPON_ISSUANCE_NOT_AVAILABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
class CouponTest {

    @Nested
    @DisplayName("쿠폰 발급 테스트")
    class IssueCouponTest {
        @Test
        void 발급_가능한_쿠폰_수를_초과할_경우_살패() {
            // given
            Coupon coupon = new CouponFixture()
                .setMaxQuantity(100)
                .setIssuedQuantity(100)
                .create();

            // when
            Throwable throwable = catchThrowable(() -> coupon.issue(1L));

            // then
            assertThat(throwable).isInstanceOf(DomainException.class)
                .hasMessage(COUPON_EXHAUSTED.message());
        }

        @Test
        void 발급_가능한_시간이_아닐경우_실패() {
            // given
            Coupon coupon = new CouponFixture()
                .setIssueStartTime(LocalDateTime.now().plusHours(1))
                .setIssueEndTime(LocalDateTime.now().plusHours(2))
                .create();

            // when
            Throwable throwable = catchThrowable(() -> coupon.issue(1L));

            // then
            assertThat(throwable).isInstanceOf(DomainException.class)
                .hasMessage(COUPON_ISSUANCE_NOT_AVAILABLE.message());
        }

        @Test
        void 성공() {
            // given
            long userId = 1L;
            Coupon coupon = new CouponFixture()
                .setMaxQuantity(100)
                .setIssuedQuantity(0)
                .create();

            // when
            IssuedCoupon result = coupon.issue(userId);

            // then
            assertThat(coupon.issuedQuantity()).isEqualTo(1);
            assertThat(result.coupon()).isEqualTo(coupon);
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.expiryDate()).isEqualTo(LocalDate.now().plusDays(coupon.expiryDays()));
            assertThat(result.usedAt()).isNull();
        }
    }
} 