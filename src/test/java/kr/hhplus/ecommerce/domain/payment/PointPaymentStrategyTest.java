package kr.hhplus.ecommerce.domain.payment;

import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.point.*;
import kr.hhplus.ecommerce.domain.user.User;
import kr.hhplus.ecommerce.domain.user.UserFixture;
import kr.hhplus.ecommerce.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static kr.hhplus.ecommerce.common.support.DomainStatus.USER_NOT_FOUND;
import static kr.hhplus.ecommerce.common.support.DomainStatus.USER_POINT_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PointPaymentStrategyTest {
    @InjectMocks
    private PointPaymentStrategy strategy;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserPointRepository userPointRepository;
    @Mock
    private UserPointHistoryRepository userPointHistoryRepository;

    @Nested
    @DisplayName("포인트 결제 전략 테스트")
    class ProcessTest {
        @Test
        void 성공() {
            // given
            Payment payment = new PaymentFixture().create();
            User user = new UserFixture().setId(payment.userId()).create();
            UserPoint userPoint = new UserPointFixture().setUserId(user.id()).create();
            given(userRepository.findById(user.id())).willReturn(Optional.of(user));
            given(userPointRepository.findByUserId(user.id()))
                .willReturn(Optional.of(userPoint));
            given(userPointRepository.save(any(UserPoint.class)))
                .willReturn(userPoint);

            // when
            strategy.process(payment);

            // then
            verify(userRepository).findById(user.id());
            verify(userPointRepository).findByUserId(user.id());
            verify(userPointRepository).save(any(UserPoint.class));
            verify(userPointHistoryRepository).save(any(UserPointHistory.class));
        }

        @Test
        void 탈퇴한_유저이면_실패() {
            // given
            Payment payment = new PaymentFixture().create();
            User user = new UserFixture().setId(payment.userId()).setWithdrawnAt(LocalDateTime.now()).create();
            given(userRepository.findById(user.id())).willReturn(Optional.of(user));

            // when
            Throwable throwable = catchThrowable(() -> strategy.process(payment));

            // then
            assertThat(throwable).isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("status", USER_NOT_FOUND)
                .hasMessage(USER_NOT_FOUND.message());
            verify(userRepository).findById(user.id());
            verify(userPointRepository, times(0)).findByUserId(user.id());
            verify(userPointRepository, times(0)).save(any(UserPoint.class));
            verify(userPointHistoryRepository, times(0)).save(any(UserPointHistory.class));
        }

        @Test
        void 유저_포인트가_존재하지_않으면_실패() {
            // given
            Payment payment = new PaymentFixture().create();
            User user = new UserFixture().setId(payment.userId()).create();
            given(userRepository.findById(user.id())).willReturn(Optional.of(user));
            given(userPointRepository.findByUserId(user.id()))
                .willReturn(Optional.empty());

            // when
            Throwable throwable = catchThrowable(() -> strategy.process(payment));

            // then
            assertThat(throwable).isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("status", USER_POINT_NOT_FOUND)
                .hasMessage(USER_POINT_NOT_FOUND.message());
            verify(userRepository).findById(user.id());
            verify(userPointRepository).findByUserId(user.id());
            verify(userPointRepository, times(0)).save(any(UserPoint.class));
            verify(userPointHistoryRepository, times(0)).save(any(UserPointHistory.class));
        }
    }

    @Nested
    @DisplayName("포인트 결제 전략 지원 여부 테스트")
    class IsSupportedTest {
        @Test
        void 포인트_결제면_지원() {
            // given
            Payment payment = new PaymentFixture()
                .setMethod(Payment.Method.POINT)
                .create();

            // when
            boolean isSupported = strategy.isSupported(payment.method());

            // then
            assertThat(isSupported).isTrue();
        }

        @ParameterizedTest
        @EnumSource(value = Payment.Method.class, names = {"CARD"})
        void 지원하지_않는_결제_방법이면_지원하지_않음(Payment.Method method) {
            // given
            Payment payment = new PaymentFixture()
                .setMethod(method)
                .create();

            // when
            boolean isSupported = strategy.isSupported(payment.method());

            // then
            assertThat(isSupported).isFalse();
        }
    }
}