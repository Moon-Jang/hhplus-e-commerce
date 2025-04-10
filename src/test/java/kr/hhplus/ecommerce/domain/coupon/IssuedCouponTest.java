package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static kr.hhplus.ecommerce.common.support.DomainStatus.ALREADY_USED_COUPON;
import static kr.hhplus.ecommerce.common.support.DomainStatus.EXPIRED_COUPON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class IssuedCouponTest {

    @Nested
    @DisplayName("쿠폰 사용 테스트")
    class UseTest {
        @Test
        void 만료되지_않은_쿠폰_사용_성공() {
            // given
            IssuedCoupon issuedCoupon = new IssuedCouponFixture()
                .setExpiryDate(LocalDate.now().plusDays(1))
                .create();

            // when
            issuedCoupon.use();

            // then
            assertThat(issuedCoupon.usedAt()).isNotNull();
        }

        @Test
        void 만료된_쿠폰_사용_실패() {
            // given
            IssuedCoupon issuedCoupon = new IssuedCouponFixture()
                .setExpiryDate(LocalDate.now().minusDays(1))
                .create();

            // when
            Throwable throwable = catchThrowable(issuedCoupon::use);

            // then
            assertThat(throwable).isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", EXPIRED_COUPON)
                .hasMessageContaining(EXPIRED_COUPON.message());
        }

        @Test
        void 이미_사용된_쿠폰_사용_실패() {
            // given
            IssuedCoupon issuedCoupon = new IssuedCouponFixture()
                .setUsedAt(LocalDateTime.now().minusDays(1))
                .create();

            // when
            Throwable throwable = catchThrowable(issuedCoupon::use);

            // then
            assertThat(throwable).isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", ALREADY_USED_COUPON)
                .hasMessage(ALREADY_USED_COUPON.message());
        }
    }

    @Nested
    @DisplayName("쿠폰 사용 확인 테스트")
    class IsUsedTest {
        @Test
        void 사용된_쿠폰_확인() {
            // given
            IssuedCoupon issuedCoupon = new IssuedCouponFixture()
                .setUsedAt(LocalDateTime.now())
                .create();

            // when
            boolean result = issuedCoupon.isUsed();

            // then
            assertThat(result).isTrue();
        }

        @Test
        void 사용되지_않은_쿠폰_확인() {
            // given
            IssuedCoupon issuedCoupon = new IssuedCouponFixture()
                .setUsedAt(null)
                .create();

            // when
            boolean result = issuedCoupon.isUsed();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("쿠폰 만료 확인 테스트")
    class IsExpiredTest {
        @Test
        void 만료된_쿠폰_확인() {
            // given
            IssuedCoupon issuedCoupon = new IssuedCouponFixture()
                .setExpiryDate(LocalDate.now().minusDays(1))
                .create();

            // when
            boolean result = issuedCoupon.isExpired();

            // then
            assertThat(result).isTrue();
        }

        @Test
        void 만료되지_않은_쿠폰_확인() {
            // given
            IssuedCoupon issuedCoupon = new IssuedCouponFixture()
                .setExpiryDate(LocalDate.now().plusDays(1))
                .create();

            // when
            boolean result = issuedCoupon.isExpired();

            // then
            assertThat(result).isFalse();
        }
    }

} 