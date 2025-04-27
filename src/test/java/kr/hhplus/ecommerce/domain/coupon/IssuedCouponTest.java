package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.domain.common.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static kr.hhplus.ecommerce.domain.common.DomainStatus.ALREADY_USED_COUPON;
import static kr.hhplus.ecommerce.domain.common.DomainStatus.EXPIRED_COUPON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class IssuedCouponTest {

    @Nested
    @DisplayName("생성자 메서드")
    class ConstructorTest {

        @Test
        @DisplayName("쿠폰이 발급되면 사용 시간이 null이다")
        void 쿠폰이_발급되면_사용_시간이_null이다() {
            // given
            long userId = 1L;
            LocalDate expiryDate = LocalDate.now().plusDays(7);
            Coupon coupon = new CouponFixture().create();

            // when
            IssuedCoupon issuedCoupon = new IssuedCoupon(userId, expiryDate, coupon);

            // then
            assertThat(issuedCoupon.usedAt()).isNull();
            assertThat(issuedCoupon.isUsed()).isFalse();
        }
    }

    @Nested
    @DisplayName("isExpired 메서드")
    class IsExpiredTest {

        @Test
        @DisplayName("만료일이 지났으면 true를 반환한다")
        void 만료일이_지났으면_true를_반환한다() {
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
        @DisplayName("만료일이 오늘이면 false를 반환한다")
        void 만료일이_오늘이면_false를_반환한다() {
            // given
            IssuedCoupon issuedCoupon = new IssuedCouponFixture()
                    .setExpiryDate(LocalDate.now())
                    .create();

            // when
            boolean result = issuedCoupon.isExpired();

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("만료일이 지나지 않았으면 false를 반환한다")
        void 만료일이_지나지_않았으면_false를_반환한다() {
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

    @Nested
    @DisplayName("isUsed 메서드")
    class IsUsedTest {

        @Test
        @DisplayName("사용 시간이 null이 아니면 true를 반환한다")
        void 사용_시간이_null이_아니면_true를_반환한다() {
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
        @DisplayName("사용 시간이 null이면 false를 반환한다")
        void 사용_시간이_null이면_false를_반환한다() {
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
    @DisplayName("use 메서드")
    class UseTest {

        @Test
        @DisplayName("이미 사용된 쿠폰이면 예외가 발생한다")
        void 이미_사용된_쿠폰이면_예외가_발생한다() {
            // given
            IssuedCoupon issuedCoupon = new IssuedCouponFixture()
                    .setUsedAt(LocalDateTime.now())
                    .create();

            // when
            Throwable throwable = catchThrowable(issuedCoupon::use);

            // then
            assertThat(throwable)
                    .isInstanceOf(DomainException.class)
                    .hasFieldOrPropertyWithValue("status", ALREADY_USED_COUPON);
        }

        @Test
        @DisplayName("만료된 쿠폰이면 예외가 발생한다")
        void 만료된_쿠폰이면_예외가_발생한다() {
            // given
            IssuedCoupon issuedCoupon = new IssuedCouponFixture()
                    .setExpiryDate(LocalDate.now().minusDays(1))
                    .create();

            // when
            Throwable throwable = catchThrowable(issuedCoupon::use);

            // then
            assertThat(throwable)
                    .isInstanceOf(DomainException.class)
                    .hasFieldOrPropertyWithValue("status", EXPIRED_COUPON);
        }

        @Test
        @DisplayName("쿠폰 사용에 성공하면 사용 시간이 설정된다")
        void 쿠폰_사용에_성공하면_사용_시간이_설정된다() {
            // given
            IssuedCoupon issuedCoupon = new IssuedCouponFixture()
                    .setUsedAt(null)
                    .setExpiryDate(LocalDate.now().plusDays(1))
                    .create();

            // when
            issuedCoupon.use();

            // then
            assertThat(issuedCoupon.usedAt()).isNotNull();
            assertThat(issuedCoupon.isUsed()).isTrue();
        }
    }
} 