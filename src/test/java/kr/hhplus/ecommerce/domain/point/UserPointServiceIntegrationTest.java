package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.common.IntegrationTestContext;
import kr.hhplus.ecommerce.common.exception.DomainException;
import kr.hhplus.ecommerce.common.exception.NotFoundException;
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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static kr.hhplus.ecommerce.common.support.DomainStatus.*;
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
        void 잔액_부족시_동시_포인트_사용_실패() throws InterruptedException {
            // given
            int initialAmount = 500;
            UserPoint userPoint = initUserPoint(new UserPointFixture().setAmount(initialAmount));

            int threadCount = 10;
            int useAmount = 100; // 총 사용 시도: 10 * 100 = 1000 > 초기값 500

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // when
            runConcurrent(threadCount, (index) -> {
                try {
                    UserPointCommand.Use command = new UserPointCommand.Use(user.id(), useAmount);
                    userPointService.use(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });

            // then
            UserPoint resultPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();

            // 5개만 성공할 수 있음 (500 / 100 = 5)
            assertThat(successCount.get()).isEqualTo(5);
            assertThat(failCount.get()).isEqualTo(5);
            assertThat(resultPoint.amount()).isEqualTo(0); // 모든 잔액이 소진됨

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(user.id());
            assertThat(histories).hasSize(5); // 성공한 트랜잭션에 대한 히스토리만 생성
            assertThat(histories).allMatch(h -> h.type() == UserPointHistory.Type.USE && h.amount() == useAmount);
        }

        @Test
        void 포인트_충전시_최대_잔액에_도달할시_충전_실패() throws InterruptedException {
            // given
            UserPoint userPoint = initUserPoint(new UserPointFixture().setAmount(UserPoint.MAX_BALANCE - 300));
            int threadCount = 10;
            int chargeAmount = 100; // 총 충전 시도: 10 * 100 = 1000 > 최대값 1000

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // when
            runConcurrent(threadCount, (index) -> {
                try {
                    UserPointCommand.Charge command = new UserPointCommand.Charge(user.id(), chargeAmount);
                    userPointService.charge(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });

            // then
            UserPoint resultPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();
            assertThat(successCount.get()).isEqualTo(3); // 3개만 성공할 수 있음 (300 / 100 = 3)
            assertThat(failCount.get()).isEqualTo(7); // 나머지 7개는 실패
            assertThat(resultPoint.amount()).isEqualTo(UserPoint.MAX_BALANCE); // 최대 잔액에 도달함
            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(user.id());
            assertThat(histories).hasSize(3); // 성공한 트랜잭션에 대한 히스토리만 생성
            assertThat(histories).allMatch(h -> h.type() == UserPointHistory.Type.CHARGE && h.amount() == chargeAmount);
        }

        @Test
        void 여러_스레드가_동시에_포인트_충전시_정확히_합산된다() throws InterruptedException {
            // given
            UserPoint userPoint = initUserPoint();
            int initialAmount = userPoint.amount();
            int threadCount = 10;
            int chargeAmount = 100;
            int expectedTotalAmount = initialAmount + (chargeAmount * threadCount);

            // when
            runConcurrent(threadCount, () -> {
                UserPointCommand.Charge command = new UserPointCommand.Charge(user.id(), chargeAmount);
                userPointService.charge(command);
            });

            // then
            UserPoint resultPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();
            assertThat(resultPoint.amount()).isEqualTo(expectedTotalAmount);

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(user.id());
            assertThat(histories).hasSize(threadCount);
            assertThat(histories).allMatch(h -> h.type() == UserPointHistory.Type.CHARGE);
            assertThat(histories).allMatch(h -> h.amount() == chargeAmount);
        }

        @Test
        void 여러_스레드가_동시에_포인트_사용시_정확히_차감된다() throws InterruptedException {
            // given
            UserPoint userPoint = initUserPoint(new UserPointFixture().setAmount(10000));
            int initialAmount = userPoint.amount();
            int threadCount = 10;
            int useAmount = 100;
            int expectedTotalAmount = initialAmount - (useAmount * threadCount);

            // when
            runConcurrent(threadCount, () -> {
                UserPointCommand.Use command = new UserPointCommand.Use(user.id(), useAmount);
                userPointService.use(command);
            });

            // then
            UserPoint resultPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();
            assertThat(resultPoint.amount()).isEqualTo(expectedTotalAmount);

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(user.id());
            assertThat(histories).hasSize(threadCount);
            assertThat(histories).allMatch(h -> h.type() == UserPointHistory.Type.USE);
            assertThat(histories).allMatch(h -> h.amount() == useAmount);
        }

        @Test
        void 서로_다른_값의_포인트_충전시_정확히_합산된다() throws InterruptedException {
            // given
            UserPoint userPoint = initUserPoint();
            int initialAmount = userPoint.amount();
            
            int[] chargeAmounts = {100, 200, 300, 400, 500};
            int expectedTotalChargeAmount = 0;
            for (int amount : chargeAmounts) {
                expectedTotalChargeAmount += amount;
            }
            int expectedTotalAmount = initialAmount + expectedTotalChargeAmount;

            // when
            runConcurrent(chargeAmounts.length, (index) -> {
                UserPointCommand.Charge command = new UserPointCommand.Charge(user.id(), chargeAmounts[index]);
                userPointService.charge(command);
            });

            // then
            UserPoint resultPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();
            assertThat(resultPoint.amount()).isEqualTo(expectedTotalAmount);

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(user.id());
            assertThat(histories).hasSize(chargeAmounts.length);
            
            // 각 충전 금액별로 히스토리가 정확히 존재하는지 확인
            for (int amount : chargeAmounts) {
                assertThat(histories.stream()
                    .filter(h -> h.type() == UserPointHistory.Type.CHARGE && h.amount() == amount)
                    .count()).isEqualTo(1);
            }
        }

        @Test
        void 서로_다른_값의_포인트_사용시_정확히_차감된다() throws InterruptedException {
            // given
            UserPoint userPoint = initUserPoint(new UserPointFixture().setAmount(10000));
            int initialAmount = userPoint.amount();
            
            int[] useAmounts = {100, 200, 300, 400, 500};
            int expectedTotalUseAmount = 0;
            for (int amount : useAmounts) {
                expectedTotalUseAmount += amount;
            }
            int expectedTotalAmount = initialAmount - expectedTotalUseAmount;

            // when
            runConcurrent(useAmounts.length, (index) -> {
                UserPointCommand.Use command = new UserPointCommand.Use(user.id(), useAmounts[index]);
                userPointService.use(command);
            });

            // then
            UserPoint resultPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();
            assertThat(resultPoint.amount()).isEqualTo(expectedTotalAmount);

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(user.id());
            assertThat(histories).hasSize(useAmounts.length);
            
            // 각 사용 금액별로 히스토리가 정확히 존재하는지 확인
            for (int amount : useAmounts) {
                assertThat(histories.stream()
                    .filter(h -> h.type() == UserPointHistory.Type.USE && h.amount() == amount)
                    .count()).isEqualTo(1);
            }
        }

        @Test
        void 충전과_사용이_동시에_발생할때_정확한_잔액을_유지한다() throws InterruptedException {
            // given
            UserPoint userPoint = initUserPoint(new UserPointFixture().setAmount(5000));
            int initialAmount = userPoint.amount();
            
            int chargeAmount = 200;
            int useAmount = 100;
            
            int chargeThreadCount = 5; // 총 충전: 200 * 5 = 1000
            int useThreadCount = 10;   // 총 사용: 100 * 10 = 1000
            
            int expectedFinalAmount = initialAmount + (chargeAmount * chargeThreadCount) - (useAmount * useThreadCount);
            
            Runnable[] tasks = new Runnable[chargeThreadCount + useThreadCount];
            
            // 충전 태스크 설정
            for (int i = 0; i < chargeThreadCount; i++) {
                tasks[i] = () -> {
                    UserPointCommand.Charge command = new UserPointCommand.Charge(user.id(), chargeAmount);
                    userPointService.charge(command);
                };
            }
            
            // 사용 태스크 설정
            for (int i = 0; i < useThreadCount; i++) {
                tasks[chargeThreadCount + i] = () -> {
                    UserPointCommand.Use command = new UserPointCommand.Use(user.id(), useAmount);
                    userPointService.use(command);
                };
            }

            // when
            runConcurrent(tasks);

            // then
            UserPoint resultPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();
            assertThat(resultPoint.amount()).isEqualTo(expectedFinalAmount);

            List<UserPointHistory> histories = userPointHistoryJpaRepository.findAllByUserId(user.id());
            assertThat(histories).hasSize(chargeThreadCount + useThreadCount);
            
            // 충전 히스토리 확인
            long chargeHistoryCount = histories.stream()
                .filter(h -> h.type() == UserPointHistory.Type.CHARGE)
                .count();
            assertThat(chargeHistoryCount).isEqualTo(chargeThreadCount);
            
            // 사용 히스토리 확인
            long useHistoryCount = histories.stream()
                .filter(h -> h.type() == UserPointHistory.Type.USE)
                .count();
            assertThat(useHistoryCount).isEqualTo(useThreadCount);
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