package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.common.IntegrationTestContext;
import kr.hhplus.ecommerce.common.exception.BadRequestException;
import kr.hhplus.ecommerce.common.exception.DomainException;
import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.user.User;
import kr.hhplus.ecommerce.domain.user.UserFixture;
import kr.hhplus.ecommerce.infrastructure.coupon.CouponJpaRepository;
import kr.hhplus.ecommerce.infrastructure.coupon.IssuedCouponJpaRepository;
import kr.hhplus.ecommerce.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static kr.hhplus.ecommerce.common.support.DomainStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class IssuedCouponServiceIntegrationTest extends IntegrationTestContext {
    @Autowired
    private IssuedCouponService issuedCouponService;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private CouponJpaRepository couponJpaRepository;
    @Autowired
    private IssuedCouponJpaRepository issuedCouponJpaRepository;

    private User user;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        user = userJpaRepository.save(new UserFixture().setId(null).create());
        coupon = couponJpaRepository.save(new CouponFixture().setId(null).create());
    }

    @Nested
    @DisplayName("쿠폰 발급 테스트")
    class IssueTest {
        @Test
        @Transactional
        void 쿠폰_발급_성공() {
            // given
            CouponCommand.Issue command = new CouponCommand.Issue(user.id(), coupon.id());
            int expectedIssuedQuantity = coupon.issuedQuantity() + 1;

            // when
            IssuedCouponVo result = issuedCouponService.issue(command);

            // then
            assertThat(result.userId()).isEqualTo(user.id());
            assertThat(result.coupon().id()).isEqualTo(coupon.id());
            assertThat(result.isUsed()).isFalse();

            Optional<Coupon> updatedCoupon = couponJpaRepository.findById(coupon.id());
            assertThat(updatedCoupon).isPresent();
            assertThat(updatedCoupon.get().issuedQuantity()).isEqualTo(expectedIssuedQuantity);

            List<IssuedCoupon> issuedCoupons = issuedCouponJpaRepository.findByUserId(user.id());
            assertThat(issuedCoupons).hasSize(1);
            assertThat(issuedCoupons.get(0).userId()).isEqualTo(user.id());
            assertThat(issuedCoupons.get(0).coupon().id()).isEqualTo(coupon.id());
            assertThat(issuedCoupons.get(0).expiryDate()).isEqualTo(LocalDate.now().plusDays(coupon.expiryDays()));
            assertThat(issuedCoupons.get(0).isUsed()).isFalse();
            assertThat(issuedCoupons.get(0).isExpired()).isFalse();
            assertThat(issuedCoupons.get(0).usedAt()).isNull();
        }

        @Test
        void 존재하지_않는_쿠폰의_경우_실패() {
            // given
            long nonExistentCouponId = 9999L;
            CouponCommand.Issue command = new CouponCommand.Issue(user.id(), nonExistentCouponId);

            // when
            Throwable throwable = catchThrowable(() -> issuedCouponService.issue(command));

            // then
            assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("status", COUPON_NOT_FOUND)
                .hasMessage(COUPON_NOT_FOUND.message());

            List<IssuedCoupon> issuedCoupons = issuedCouponJpaRepository.findByUserId(user.id());
            assertThat(issuedCoupons).isEmpty();
        }

        @Test
        void 발급_가능_수량_초과시_실패() {
            // given
            Coupon exhaustedCoupon = couponJpaRepository.save(new CouponFixture()
                .setId(null)
                .setIssueStartTime(LocalDateTime.now().minusHours(1))
                .setIssueEndTime(LocalDateTime.now().plusHours(1))
                .setMaxQuantity(1)
                .setIssuedQuantity(1)
                .create());

            CouponCommand.Issue command = new CouponCommand.Issue(user.id(), exhaustedCoupon.id());

            // when
            Throwable throwable = catchThrowable(() -> issuedCouponService.issue(command));

            // then
            assertThat(throwable)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", COUPON_EXHAUSTED);

            Optional<Coupon> updatedCoupon = couponJpaRepository.findById(exhaustedCoupon.id());
            assertThat(updatedCoupon).isPresent();
            assertThat(updatedCoupon.get().issuedQuantity()).isEqualTo(exhaustedCoupon.issuedQuantity());

            List<IssuedCoupon> issuedCoupons = issuedCouponJpaRepository.findByUserId(user.id());
            assertThat(issuedCoupons).isEmpty();
        }

        @Test
        void 발급_기간_이전인_경우_실패() {
            // given
            Coupon futureCoupon = couponJpaRepository.save(new CouponFixture()
                .setId(null)
                .setIssueStartTime(LocalDateTime.now().plusHours(1))
                .setIssueEndTime(LocalDateTime.now().plusHours(2))
                .create());

            CouponCommand.Issue command = new CouponCommand.Issue(user.id(), futureCoupon.id());

            // when
            Throwable throwable = catchThrowable(() -> issuedCouponService.issue(command));

            // then
            assertThat(throwable)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", COUPON_ISSUANCE_NOT_AVAILABLE);

            Optional<Coupon> updatedCoupon = couponJpaRepository.findById(futureCoupon.id());
            assertThat(updatedCoupon).isPresent();
            assertThat(updatedCoupon.get().issuedQuantity()).isEqualTo(futureCoupon.issuedQuantity());

            List<IssuedCoupon> issuedCoupons = issuedCouponJpaRepository.findByUserId(user.id());
            assertThat(issuedCoupons).isEmpty();
        }

        @Test
        void 발급_기간_이후인_경우_실패() {
            // given
            Coupon expiredCoupon = couponJpaRepository.save(new CouponFixture()
                .setId(null)
                .setIssueStartTime(LocalDateTime.now().minusHours(2))
                .setIssueEndTime(LocalDateTime.now().minusHours(1))
                .create());

            CouponCommand.Issue command = new CouponCommand.Issue(user.id(), expiredCoupon.id());

            // when
            Throwable throwable = catchThrowable(() -> issuedCouponService.issue(command));

            // then
            assertThat(throwable)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", COUPON_ISSUANCE_NOT_AVAILABLE);

            Optional<Coupon> updatedCoupon = couponJpaRepository.findById(expiredCoupon.id());
            assertThat(updatedCoupon).isPresent();
            assertThat(updatedCoupon.get().issuedQuantity()).isEqualTo(expiredCoupon.issuedQuantity());

            List<IssuedCoupon> issuedCoupons = issuedCouponJpaRepository.findByUserId(user.id());
            assertThat(issuedCoupons).isEmpty();
        }

        @Test
        void 존재하지_않는_사용자의_경우_실패() {
            // given
            long nonExistentUserId = 9999L;
            CouponCommand.Issue command = new CouponCommand.Issue(nonExistentUserId, coupon.id());

            // when
            Throwable throwable = catchThrowable(() -> issuedCouponService.issue(command));

            // then
            assertThat(throwable)
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("status", USER_NOT_FOUND)
                .hasMessage(USER_NOT_FOUND.message());

            Optional<Coupon> updatedCoupon = couponJpaRepository.findById(coupon.id());
            assertThat(updatedCoupon).isPresent();
            assertThat(updatedCoupon.get().issuedQuantity()).isEqualTo(coupon.issuedQuantity());

            List<IssuedCoupon> issuedCoupons = issuedCouponJpaRepository.findByUserId(nonExistentUserId);
            assertThat(issuedCoupons).isEmpty();
        }

        @Test
        void 비활성화된_사용자의_경우_실패() {
            // given
            User inactiveUser = new UserFixture()
                .setId(null)
                .setWithdrawnAt(LocalDateTime.now())
                .create();
            userJpaRepository.save(inactiveUser);

            CouponCommand.Issue command = new CouponCommand.Issue(inactiveUser.id(), coupon.id());

            // when
            Throwable throwable = catchThrowable(() -> issuedCouponService.issue(command));

            // then
            assertThat(throwable)
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("status", USER_NOT_FOUND)
                .hasMessage(USER_NOT_FOUND.message());

            Optional<Coupon> updatedCoupon = couponJpaRepository.findById(coupon.id());
            assertThat(updatedCoupon).isPresent();
            assertThat(updatedCoupon.get().issuedQuantity()).isEqualTo(coupon.issuedQuantity());

            List<IssuedCoupon> issuedCoupons = issuedCouponJpaRepository.findByUserId(inactiveUser.id());
            assertThat(issuedCoupons).isEmpty();
        }
    }

    @Nested
    @DisplayName("쿠폰 사용 테스트")
    class UseTest {
        private IssuedCoupon issuedCoupon;

        @BeforeEach
        void setUp() {
            // 각 테스트 전에 쿠폰 발급
            CouponCommand.Issue command = new CouponCommand.Issue(user.id(), coupon.id());
            IssuedCouponVo issuedCouponVo = issuedCouponService.issue(command);
            issuedCoupon = issuedCouponJpaRepository.findById(issuedCouponVo.id()).orElseThrow();
        }

        @Test
        void 쿠폰_사용_성공() {
            // given
            long issuedCouponId = issuedCoupon.id();

            // when
            issuedCouponService.use(issuedCouponId);

            // then
            Optional<IssuedCoupon> updatedCoupon = issuedCouponJpaRepository.findById(issuedCouponId);
            assertThat(updatedCoupon).isPresent();
            assertThat(updatedCoupon.get().isUsed()).isTrue();
            assertThat(updatedCoupon.get().usedAt()).isNotNull();
        }

        @Test
        void 존재하지_않는_쿠폰_사용시_실패() {
            // given
            long nonExistentCouponId = 9999L;

            // when
            Throwable throwable = catchThrowable(() -> issuedCouponService.use(nonExistentCouponId));

            // then
            assertThat(throwable)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", ISSUED_COUPON_NOT_FOUND);
        }

        @Test
        void 이미_사용한_쿠폰_재사용시_실패() {
            // given
            long issuedCouponId = issuedCoupon.id();

            // 우선 한번 사용
            issuedCouponService.use(issuedCouponId);

            // when
            Throwable throwable = catchThrowable(() -> issuedCouponService.use(issuedCouponId));

            // then
            assertThat(throwable)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", ALREADY_USED_COUPON)
                .hasMessage(ALREADY_USED_COUPON.message());
        }

        @Test
        void 만료된_쿠폰_사용시_실패() {
            // given
            // 만료된 날짜를 가진 쿠폰 생성
            IssuedCoupon expiredCoupon = issuedCouponJpaRepository.save(
                new IssuedCouponFixture()
                    .setId(null)
                    .setUserId(user.id())
                    .setExpiryDate(LocalDate.now().minusDays(1))
                    .setCoupon(coupon)
                    .create()
            );

            // when
            Throwable throwable = catchThrowable(() -> issuedCouponService.use(expiredCoupon.id()));

            // then
            assertThat(throwable)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", EXPIRED_COUPON)
                .hasMessage(EXPIRED_COUPON.message());
        }
    }

    @Nested
    @DisplayName("동시성 테스트")
    class ConcurrencyTest {
        @Test
        void 요청이_동시에_들어올때_요청_횟수보다_쿠폰_수량이_부족할시_쿠폰_수량만큼_성공하고_나머지는_실패() throws InterruptedException {
            // given
            int maxQuantity = 5; // 최대 발급 가능 수량
            Coupon limitedCoupon = couponJpaRepository.save(
                new CouponFixture()
                    .setId(null)
                    .setMaxQuantity(maxQuantity)
                    .setIssuedQuantity(0)
                    .create()
            );

            // 10명의 사용자 생성 (최대 발급 가능 수량보다 많음)
            User[] users = new User[10];
            for (int i = 0; i < users.length; i++) {
                users[i] = userJpaRepository.save(
                    new UserFixture().setId(null).create()
                );
            }

            int threadCount = 10;
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // when
            runConcurrent(threadCount, (index) -> {
                try {
                    CouponCommand.Issue command = new CouponCommand.Issue(users[index].id(), limitedCoupon.id());
                    issuedCouponService.issue(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });

            // then
            Optional<Coupon> updatedCoupon = couponJpaRepository.findById(limitedCoupon.id());
            assertThat(updatedCoupon).isPresent();
            assertThat(updatedCoupon.get().issuedQuantity()).isEqualTo(maxQuantity);

            assertThat(successCount.get()).isEqualTo(maxQuantity); // 성공 횟수 = maxQuantity
            assertThat(failCount.get()).isEqualTo(threadCount - maxQuantity); // 실패 횟수 = (전체 요청 - 성공 횟수)
            List<IssuedCoupon> allIssuedCoupons = issuedCouponJpaRepository.findAll();
            assertThat(allIssuedCoupons).hasSize(maxQuantity);
        }

        @Test
        void 요청이_동시에_올때_요청_횟수보다_쿠폰_수량이_더_크면_모든_요청이_성공한다() throws InterruptedException {
            // given
            int initialQuantity = 0;
            Coupon limitedCoupon = couponJpaRepository.save(
                new CouponFixture()
                    .setId(null)
                    .setMaxQuantity(100)
                    .setIssuedQuantity(initialQuantity)
                    .create()
            );

            // 10명의 사용자 생성
            User[] users = new User[10];
            for (int i = 0; i < users.length; i++) {
                users[i] = userJpaRepository.save(
                    new UserFixture().setId(null).create()
                );
            }

            int threadCount = 10;

            // when
            runConcurrent(threadCount, (index) -> {
                CouponCommand.Issue command = new CouponCommand.Issue(users[index].id(), limitedCoupon.id());
                issuedCouponService.issue(command);
            });

            // then
            Optional<Coupon> updatedCoupon = couponJpaRepository.findById(limitedCoupon.id());
            assertThat(updatedCoupon).isPresent();
            assertThat(updatedCoupon.get().issuedQuantity()).isEqualTo(initialQuantity + threadCount);

            for (User testUser : users) {
                List<IssuedCoupon> issuedCoupons = issuedCouponJpaRepository.findByUserId(testUser.id());
                assertThat(issuedCoupons).hasSize(1);
                assertThat(issuedCoupons.get(0).coupon().id()).isEqualTo(limitedCoupon.id());
                assertThat(issuedCoupons.get(0).isUsed()).isFalse();
            }
        }
    }
} 