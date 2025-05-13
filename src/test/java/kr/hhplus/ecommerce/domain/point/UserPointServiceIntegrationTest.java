package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.common.IntegrationTestContext;
import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.common.DomainException;
import kr.hhplus.ecommerce.domain.user.User;
import kr.hhplus.ecommerce.domain.user.UserFixture;
import kr.hhplus.ecommerce.infrastructure.point.UserPointHistoryJpaRepository;
import kr.hhplus.ecommerce.infrastructure.point.UserPointJpaRepository;
import kr.hhplus.ecommerce.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static kr.hhplus.ecommerce.domain.common.DomainStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class UserPointServiceIntegrationTest extends IntegrationTestContext {
    @Autowired
    private UserPointService userPointService;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private UserPointJpaRepository userPointJpaRepository;
    @Autowired
    private UserPointHistoryJpaRepository userPointHistoryJpaRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userJpaRepository.save(new UserFixture().setId(null).create());
    }

    @Nested
    @DisplayName("충전 테스트")
    class ChargeTest {
        @Test
        void 충전_성공() {
            // given
            UserPoint userPoint = initUserPoint();
            int chargeAmount = 1000;
            int expectedAmount = userPoint.amount() + chargeAmount;
            UserPointCommand.Charge command = new UserPointCommand.Charge(user.id(), chargeAmount);

            // when
            UserPointVo result = userPointService.charge(command);

            // then
            assertThat(result.userId()).isEqualTo(user.id());
            assertThat(result.amount()).isEqualTo(expectedAmount);

            Optional<UserPoint> savedUserPoint = userPointJpaRepository.findByUserId(user.id());
            assertThat(savedUserPoint).isPresent();
            assertThat(savedUserPoint.get().amount()).isEqualTo(expectedAmount);

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(user.id());
            assertThat(histories).isNotEmpty();
            UserPointHistory lastHistory = histories.get(histories.size() - 1);
            assertThat(lastHistory.userId()).isEqualTo(user.id());
            assertThat(lastHistory.amount()).isEqualTo(chargeAmount);
            assertThat(lastHistory.type()).isEqualTo(UserPointHistory.Type.CHARGE);
        }

        @Test
        void 유저_포인트가_존재하지_않을_경우_생성후_충전() {
            // given
            int chargeAmount = 1000;
            UserPointCommand.Charge command = new UserPointCommand.Charge(user.id(), chargeAmount);

            // when
            UserPointVo result = userPointService.charge(command);

            // then
            assertThat(result.userId()).isEqualTo(user.id());
            assertThat(result.amount()).isEqualTo(chargeAmount);

            Optional<UserPoint> savedUserPoint = userPointJpaRepository.findByUserId(user.id());
            assertThat(savedUserPoint).isPresent();
            assertThat(savedUserPoint.get().amount()).isEqualTo(chargeAmount);

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(user.id());
            assertThat(histories).isNotEmpty();
            UserPointHistory lastHistory = histories.get(histories.size() - 1);
            assertThat(lastHistory.userId()).isEqualTo(user.id());
            assertThat(lastHistory.amount()).isEqualTo(chargeAmount);
            assertThat(lastHistory.type()).isEqualTo(UserPointHistory.Type.CHARGE);
        }

        @Test
        void 존재하지_않는_사용자의_경우_실패() {
            // given
            long nonExistentUserId = 9999L;
            UserPointCommand.Charge command = new UserPointCommand.Charge(nonExistentUserId, 1000);

            // when
            Throwable throwable = catchThrowable(() -> userPointService.charge(command));

            // then
            assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("status", USER_NOT_FOUND)
                .hasMessage(USER_NOT_FOUND.message());

            Optional<UserPoint> savedUserPoint = userPointJpaRepository.findByUserId(nonExistentUserId);
            assertThat(savedUserPoint).isEmpty();

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(nonExistentUserId);
            assertThat(histories).isEmpty();
        }

        @Test
        void 비활성화된_사용자의_경우_실패() {
            // given
            User inactiveUser = new UserFixture()
                .setId(null)
                .setWithdrawnAt(java.time.LocalDateTime.now())
                .create();
            userJpaRepository.save(inactiveUser);

            UserPointCommand.Charge command = new UserPointCommand.Charge(inactiveUser.id(), 1000);

            // when
            Throwable throwable = catchThrowable(() -> userPointService.charge(command));

            // then
            assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("status", USER_NOT_FOUND)
                .hasMessage(USER_NOT_FOUND.message());

            Optional<UserPoint> savedUserPoint = userPointJpaRepository.findByUserId(inactiveUser.id());
            assertThat(savedUserPoint).isEmpty();

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(inactiveUser.id());
            assertThat(histories).isEmpty();
        }

        @Test
        void 최소_충전_금액보다_적은_경우_실패() {
            // given
            int belowMinAmount = UserPoint.MIN_CHARGE_AMOUNT - 1;
            UserPoint userPoint = initUserPoint();
            int initialAmount = userPoint.amount();
            UserPointCommand.Charge command = new UserPointCommand.Charge(user.id(), belowMinAmount);

            // when
            Throwable throwable = catchThrowable(() -> userPointService.charge(command));

            // then
            assertThat(throwable)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", INVALID_CHARGE_AMOUNT)
                .hasMessage(INVALID_CHARGE_AMOUNT.message());

            UserPoint resultPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();
            assertThat(resultPoint.amount()).isEqualTo(initialAmount);

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(user.id());
            assertThat(histories).isEmpty();
        }

        @Test
        void 최대_충전_금액보다_많은_경우_실패() {
            // given
            int aboveMaxAmount = UserPoint.MAX_CHARGE_AMOUNT + 1;
            UserPoint userPoint = initUserPoint();
            int initialAmount = userPoint.amount();
            UserPointCommand.Charge command = new UserPointCommand.Charge(user.id(), aboveMaxAmount);

            // when
            Throwable throwable = catchThrowable(() -> userPointService.charge(command));

            // then
            assertThat(throwable)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", INVALID_CHARGE_AMOUNT);

            UserPoint resultPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();
            assertThat(resultPoint.amount()).isEqualTo(initialAmount);

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(user.id());
            assertThat(histories).isEmpty();
        }

        @Test
        void 충전_후_잔액이_최대치를_넘는_경우_실패() {
            // given
            int nearMaxAmount = UserPoint.MAX_BALANCE - 100;
            UserPoint userPoint = initUserPoint(new UserPointFixture().setAmount(nearMaxAmount));

            int chargeAmount = 200;
            UserPointCommand.Charge command = new UserPointCommand.Charge(user.id(), chargeAmount);

            // when
            Throwable throwable = catchThrowable(() -> userPointService.charge(command));

            // then
            assertThat(throwable)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", EXCEEDED_MAX_USER_POINT);

            UserPoint resultPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();
            assertThat(resultPoint.amount()).isEqualTo(nearMaxAmount);

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(user.id());
            assertThat(histories).isEmpty();
        }
    }

    @Nested
    @DisplayName("사용 테스트")
    class UseTest {

        @Test
        void 포인트_사용_성공() {
            // given
            UserPoint userPoint = initUserPoint();

            int useAmount = 1000;
            int expectedAmount = userPoint.amount() - useAmount;
            UserPointCommand.Use command = new UserPointCommand.Use(user.id(), useAmount);

            // when
            UserPointVo result = userPointService.use(command);

            // then
            assertThat(result.userId()).isEqualTo(user.id());
            assertThat(result.amount()).isEqualTo(expectedAmount);
            
            Optional<UserPoint> savedUserPoint = userPointJpaRepository.findByUserId(user.id());
            assertThat(savedUserPoint).isPresent();
            assertThat(savedUserPoint.get().amount()).isEqualTo(expectedAmount);

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(user.id());
            assertThat(histories).isNotEmpty();
            UserPointHistory lastHistory = histories.get(histories.size() - 1);
            assertThat(lastHistory.userId()).isEqualTo(user.id());
            assertThat(lastHistory.amount()).isEqualTo(useAmount);
            assertThat(lastHistory.type()).isEqualTo(UserPointHistory.Type.USE);
        }

        @Test
        void 존재하지_않는_사용자의_경우_실패() {
            // given
            long nonExistentUserId = 9999L;
            UserPointCommand.Use command = new UserPointCommand.Use(nonExistentUserId, 1000);

            // when
            Throwable throwable = catchThrowable(() -> userPointService.use(command));

            // then
            assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("status", USER_NOT_FOUND)
                .hasMessage(USER_NOT_FOUND.message());

            Optional<UserPoint> savedUserPoint = userPointJpaRepository.findByUserId(nonExistentUserId);
            assertThat(savedUserPoint).isEmpty();

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(nonExistentUserId);
            assertThat(histories).isEmpty();
        }

        @Test
        void 비활성화된_사용자의_경우_실패() {
            // given
            User inactiveUser = new UserFixture()
                .setId(null)
                .setWithdrawnAt(java.time.LocalDateTime.now())
                .create();
            userJpaRepository.save(inactiveUser);

            UserPoint userPoint = new UserPointFixture()
                .setId(null)
                .setUserId(inactiveUser.id())
                .setAmount(5000)
                .create();
            UserPoint savedUserPoint = userPointJpaRepository.save(userPoint);

            UserPointCommand.Use command = new UserPointCommand.Use(inactiveUser.id(), 1000);

            // when
            Throwable throwable = catchThrowable(() -> userPointService.use(command));

            // then
            assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("status", USER_NOT_FOUND)
                .hasMessage(USER_NOT_FOUND.message());

            Optional<UserPoint> userPointAfter = userPointJpaRepository.findByUserId(inactiveUser.id());
            assertThat(userPointAfter).isPresent();
            assertThat(userPointAfter.get().amount()).isEqualTo(savedUserPoint.amount());

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(inactiveUser.id());
            assertThat(histories).isEmpty();
        }

        @Test
        void 포인트가_없는_사용자의_경우_실패() {
            // given
            UserPointCommand.Use command = new UserPointCommand.Use(user.id(), 1000);

            // when
            Throwable throwable = catchThrowable(() -> userPointService.use(command));

            // then
            assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("status", USER_POINT_NOT_FOUND)
                .hasMessage(USER_POINT_NOT_FOUND.message());

            Optional<UserPoint> userPoint = userPointJpaRepository.findByUserId(user.id());
            assertThat(userPoint).isEmpty();

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(user.id());
            assertThat(histories).isEmpty();
        }

        @Test
        void 최소_사용_금액보다_적은_경우_실패() {
            // given
            UserPoint userPoint = initUserPoint();
            int belowMinAmount = UserPoint.MIN_USE_AMOUNT - 1;
            UserPointCommand.Use command = new UserPointCommand.Use(user.id(), belowMinAmount);

            // when
            Throwable throwable = catchThrowable(() -> userPointService.use(command));

            // then
            assertThat(throwable)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", INVALID_USE_AMOUNT)
                .hasMessage(INVALID_USE_AMOUNT.message());

            Optional<UserPoint> userPointAfter = userPointJpaRepository.findByUserId(user.id());
            assertThat(userPointAfter).isPresent();
            assertThat(userPointAfter.get().amount()).isEqualTo(userPoint.amount());

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(user.id());
            assertThat(histories).isEmpty();
        }

        @Test
        void 잔액이_부족한_경우_실패() {
            // given
            UserPoint userPoint = initUserPoint();
            int useAmount = userPoint.amount() + 1;
            UserPointCommand.Use command = new UserPointCommand.Use(user.id(), useAmount);

            // when
            Throwable throwable = catchThrowable(() -> userPointService.use(command));

            // then
            assertThat(throwable)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", INSUFFICIENT_BALANCE)
                .hasMessage(INSUFFICIENT_BALANCE.message());

            Optional<UserPoint> userPointAfter = userPointJpaRepository.findByUserId(user.id());
            assertThat(userPointAfter).isPresent();
            assertThat(userPointAfter.get().amount()).isEqualTo(userPoint.amount());

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(user.id());
            assertThat(histories).isEmpty();
        }
    }

    @Nested
    @DisplayName("동시성 테스트")
    class ConcurrencyTest {
        @Test
        void 동시에_충전_요청이_오면_모두_성공한다() throws InterruptedException {
            // given
            UserPoint userPoint = initUserPoint(new UserPointFixture());

            int threadCount = 3;
            int initialAmount = userPoint.amount();
            int chargeAmount = 100;

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            Queue<Exception> exceptions = new ConcurrentLinkedQueue<>();

            // when
            runConcurrent(threadCount, (index) -> {
                try {
                    UserPointCommand.Charge command = new UserPointCommand.Charge(userPoint.userId(), chargeAmount);
                    userPointService.charge(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    exceptions.add(e);
                    failCount.incrementAndGet();
                }
            });

            // then
            assertThat(successCount.get()).isEqualTo(threadCount);
            assertThat(failCount.get()).isEqualTo(threadCount - successCount.get());

            UserPoint resultPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();
            assertThat(resultPoint.amount()).isEqualTo(initialAmount + (chargeAmount * threadCount));
        }

        @Test
        void 동시에_사용_요청이_오면_하나만_성공한다() throws InterruptedException {
            // given
            int initialAmount = 5000;
            UserPoint userPoint = initUserPoint(new UserPointFixture().setAmount(initialAmount));

            int threadCount = 3;
            int useAmount = 1000;

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            Queue<Exception> exceptions = new ConcurrentLinkedQueue<>();

            // when
            runConcurrent(threadCount, (index) -> {
                try {
                    UserPointCommand.Use command = new UserPointCommand.Use(userPoint.userId(), useAmount);
                    userPointService.use(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    exceptions.add(e);
                    failCount.incrementAndGet();
                }
            });

            // then
            assertThat(successCount.get()).isEqualTo(1);
            assertThat(failCount.get()).isEqualTo(threadCount - successCount.get());
            assertThat(exceptions).hasSize(failCount.get());
            assertThat(exceptions).allMatch(e -> e instanceof ObjectOptimisticLockingFailureException);

            UserPoint resultPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();
            assertThat(resultPoint.amount()).isEqualTo(initialAmount - useAmount);
        }

        @Test
        void 충전과_사용_요청이_동시에_오면_하나만_성공한다() throws InterruptedException {
            // given
            UserPoint userPoint = initUserPoint(new UserPointFixture().setAmount(5000));
            int initialAmount = userPoint.amount();
            int chargeAmount = 200;
            int useAmount = 100;

            Runnable[] tasks = new Runnable[2];
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            Queue<Exception> exceptions = new ConcurrentLinkedQueue<>();

            // 충전 스레드
            tasks[0] = () -> {
                UserPointCommand.Charge command = new UserPointCommand.Charge(userPoint.userId(), chargeAmount);
                try {
                    userPointService.charge(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    exceptions.add(e);
                    failCount.incrementAndGet();
                }
            };

            // 사용 스레드
            tasks[1] = () -> {
                UserPointCommand.Use command = new UserPointCommand.Use(userPoint.userId(), useAmount);
                try {
                    userPointService.use(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    exceptions.add(e);
                    failCount.incrementAndGet();
                }
            };

            // when
            runConcurrent(tasks);

            // then
            assertThat(successCount.get()).isEqualTo(1);
            assertThat(failCount.get()).isEqualTo(tasks.length - successCount.get());
            assertThat(exceptions).hasSize(failCount.get());
            assertThat(exceptions).allMatch(e -> e instanceof ObjectOptimisticLockingFailureException);
        }
    }

    private UserPoint initUserPoint() {
        return userPointJpaRepository.save(
            new UserPointFixture().setUserId(user.id()).setId(null).create()
        );
    }

    private UserPoint initUserPoint(UserPointFixture fixture) {
        return userPointJpaRepository.save(
            fixture.setUserId(user.id()).setId(null).create()
        );
    }
}