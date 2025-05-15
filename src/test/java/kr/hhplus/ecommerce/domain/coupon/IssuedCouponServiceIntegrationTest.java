package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.common.IntegrationTestContext;
import kr.hhplus.ecommerce.common.exception.BadRequestException;
import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.common.DomainException;
import kr.hhplus.ecommerce.domain.user.User;
import kr.hhplus.ecommerce.domain.user.UserFixture;
import kr.hhplus.ecommerce.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static kr.hhplus.ecommerce.domain.common.DomainStatus.*;
import static kr.hhplus.ecommerce.infrastructure.coupon.CouponIssuanceRequestRepositoryImpl.COUPON_WAITING_QUEUE_KEY_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class IssuedCouponServiceIntegrationTest extends IntegrationTestContext {
    @Autowired
    private IssuedCouponService issuedCouponService;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private IssuedCouponRepository issuedCouponRepository;
    @Autowired
    private CouponIssuanceRequestRepository couponIssuanceRequestRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private User user;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        user = userJpaRepository.save(new UserFixture().setId(null).create());
        coupon = couponRepository.save(new CouponFixture().setId(null).create());
    }

    @Nested
    @DisplayName("쿠폰 발급 테스트")
    class IssueTest {
        @Test
        @Transactional
        void 쿠폰_발급_성공() {
            // given
            CouponCommand.Issue command = new CouponCommand.Issue(user.id(), coupon.id());

            // when
            IssuedCouponVo result = issuedCouponService.issue(command);

            // then
            assertThat(result.userId()).isEqualTo(user.id());
            assertThat(result.couponId()).isEqualTo(coupon.id());
            assertThat(result.isUsed()).isFalse();

            Optional<Coupon> updatedCoupon = couponRepository.findById(coupon.id());
            assertThat(updatedCoupon).isPresent();

            List<IssuedCoupon> issuedCoupons = issuedCouponRepository.findByUserId(user.id());
            assertThat(issuedCoupons).hasSize(1);
            assertThat(issuedCoupons.get(0).userId()).isEqualTo(user.id());
            assertThat(issuedCoupons.get(0).couponId()).isEqualTo(coupon.id());
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

            List<IssuedCoupon> issuedCoupons = issuedCouponRepository.findByUserId(user.id());
            assertThat(issuedCoupons).isEmpty();
        }

        @Test
        void 발급_기간_이전인_경우_실패() {
            // given
            Coupon futureCoupon = couponRepository.save(new CouponFixture()
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

            Optional<Coupon> updatedCoupon = couponRepository.findById(futureCoupon.id());
            assertThat(updatedCoupon).isPresent();
            assertThat(updatedCoupon.get().issuedQuantity()).isEqualTo(futureCoupon.issuedQuantity());

            List<IssuedCoupon> issuedCoupons = issuedCouponRepository.findByUserId(user.id());
            assertThat(issuedCoupons).isEmpty();
        }

        @Test
        void 발급_기간_이후인_경우_실패() {
            // given
            Coupon expiredCoupon = couponRepository.save(new CouponFixture()
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

            Optional<Coupon> updatedCoupon = couponRepository.findById(expiredCoupon.id());
            assertThat(updatedCoupon).isPresent();
            assertThat(updatedCoupon.get().issuedQuantity()).isEqualTo(expiredCoupon.issuedQuantity());

            List<IssuedCoupon> issuedCoupons = issuedCouponRepository.findByUserId(user.id());
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

            Optional<Coupon> updatedCoupon = couponRepository.findById(coupon.id());
            assertThat(updatedCoupon).isPresent();
            assertThat(updatedCoupon.get().issuedQuantity()).isEqualTo(coupon.issuedQuantity());

            List<IssuedCoupon> issuedCoupons = issuedCouponRepository.findByUserId(nonExistentUserId);
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

            Optional<Coupon> updatedCoupon = couponRepository.findById(coupon.id());
            assertThat(updatedCoupon).isPresent();
            assertThat(updatedCoupon.get().issuedQuantity()).isEqualTo(coupon.issuedQuantity());

            List<IssuedCoupon> issuedCoupons = issuedCouponRepository.findByUserId(inactiveUser.id());
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
            issuedCoupon = issuedCouponRepository.findById(issuedCouponVo.id()).orElseThrow();
        }

        @Test
        void 쿠폰_사용_성공() {
            // given
            long issuedCouponId = issuedCoupon.id();

            // when
            issuedCouponService.use(issuedCouponId);

            // then
            Optional<IssuedCoupon> updatedCoupon = issuedCouponRepository.findById(issuedCouponId);
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
            IssuedCoupon expiredCoupon = issuedCouponRepository.save(
                new IssuedCouponFixture()
                    .setId(null)
                    .setUserId(user.id())
                    .setExpiryDate(LocalDate.now().minusDays(1))
                    .setCouponId(coupon.id())
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
    @DisplayName("쿠폰 발급 요청 테스트")
    class RequestIssuanceTest {
        @Test
        void 쿠폰_발급_요청_성공() {
            // given
            long requestTimeMillis = System.currentTimeMillis();
            CouponCommand.RequestIssuance command = new CouponCommand.RequestIssuance(user.id(), coupon.id(), requestTimeMillis);

            // when
            issuedCouponService.requestIssuance(command);

            // then
            String key = COUPON_WAITING_QUEUE_KEY_PREFIX + coupon.id();
            Double score = redisTemplate.opsForZSet().score(key, String.valueOf(user.id()));
            assertThat(score).isNotNull();
            assertThat(score).isEqualTo((double) requestTimeMillis);
        }

        @Test
        void 이미_발급된_쿠폰인_경우_실패() {
            // given
            // 먼저 쿠폰 발급
            CouponCommand.Issue issueCommand = new CouponCommand.Issue(user.id(), coupon.id());
            issuedCouponService.issue(issueCommand);

            long requestTimeMillis = System.currentTimeMillis();
            CouponCommand.RequestIssuance command = new CouponCommand.RequestIssuance(user.id(), coupon.id(), requestTimeMillis);

            // when
            Throwable throwable = catchThrowable(() -> issuedCouponService.requestIssuance(command));

            // then
            assertThat(throwable)
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("status", COUPON_ALREADY_ISSUED);

            // Redis에 저장되지 않았는지 확인
            String key = COUPON_WAITING_QUEUE_KEY_PREFIX + coupon.id();
            Double score = redisTemplate.opsForZSet().score(key, String.valueOf(user.id()));
            assertThat(score).isNull();
        }

        @Test
        void 쿠폰_수량이_부족한_경우_실패() {
            // given
            Coupon exhaustedCoupon = couponRepository.save(
                new CouponFixture()
                    .setId(null)
                    .setMaxQuantity(1)
                    .setIssuedQuantity(1)
                    .create()
            );

            long requestTimeMillis = System.currentTimeMillis();
            CouponCommand.RequestIssuance command = new CouponCommand.RequestIssuance(user.id(), exhaustedCoupon.id(), requestTimeMillis);

            // when
            Throwable throwable = catchThrowable(() -> issuedCouponService.requestIssuance(command));

            // then
            assertThat(throwable)
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("status", COUPON_EXHAUSTED);

            // Redis에 저장되지 않았는지 확인
            String key = COUPON_WAITING_QUEUE_KEY_PREFIX + exhaustedCoupon.id();
            Double score = redisTemplate.opsForZSet().score(key, String.valueOf(user.id()));
            assertThat(score).isNull();
        }
    }

    @Nested
    @DisplayName("대기열에서 쿠폰 발급 테스트")
    class ReleaseFromWaitingQueueTest {
        @Test
        @Transactional
        void 대기열에서_쿠폰_발급_성공() {
            // given
            long requestTimeMillis = System.currentTimeMillis();
            CouponCommand.RequestIssuance command = new CouponCommand.RequestIssuance(user.id(), coupon.id(), requestTimeMillis);
            issuedCouponService.requestIssuance(command);

            // when
            issuedCouponService.releaseFromWaitingQueue();

            // then
            List<IssuedCoupon> issuedCoupons = issuedCouponRepository.findByUserId(user.id());
            assertThat(issuedCoupons).hasSize(1);
            assertThat(issuedCoupons.get(0).userId()).isEqualTo(user.id());
            assertThat(issuedCoupons.get(0).couponId()).isEqualTo(coupon.id());
            assertThat(issuedCoupons.get(0).isUsed()).isFalse();
            assertThat(issuedCoupons.get(0).isExpired()).isFalse();
            assertThat(issuedCoupons.get(0).usedAt()).isNull();

            Optional<Coupon> updatedCoupon = couponRepository.findById(coupon.id());
            assertThat(updatedCoupon).isPresent();
            assertThat(updatedCoupon.get().issuedQuantity()).isEqualTo(1);
        }

        @Test
        @Transactional
        void 여러_요청이_있는_경우_모두_처리() {
            // given
            // 여러 사용자 생성
            User[] users = new User[3];
            for (int i = 0; i < users.length; i++) {
                users[i] = userJpaRepository.save(new UserFixture().setId(null).create());
            }

            // 각 사용자별로 쿠폰 발급 요청
            long requestTimeMillis = System.currentTimeMillis();
            for (User testUser : users) {
                CouponCommand.RequestIssuance command = new CouponCommand.RequestIssuance(testUser.id(), coupon.id(), requestTimeMillis);
                issuedCouponService.requestIssuance(command);
            }

            // when
            issuedCouponService.releaseFromWaitingQueue();

            // then
            for (User testUser : users) {
                IssuedCoupon issuedCoupon = issuedCouponRepository.findByCouponIdAndUserId(coupon.id(), testUser.id())
                        .orElseThrow();
                assertThat(issuedCoupon.userId()).isEqualTo(testUser.id());
                assertThat(issuedCoupon.couponId()).isEqualTo(coupon.id());
            }

            Optional<Coupon> updatedCoupon = couponRepository.findById(coupon.id());
            assertThat(updatedCoupon).isPresent();
            assertThat(updatedCoupon.get().issuedQuantity()).isEqualTo(3);
        }
    }
}