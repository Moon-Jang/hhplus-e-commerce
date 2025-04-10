package kr.hhplus.ecommerce.domain.point;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.ecommerce.common.exception.NotFoundException;
import static kr.hhplus.ecommerce.common.support.DomainStatus.USER_NOT_FOUND;
import static kr.hhplus.ecommerce.common.support.DomainStatus.USER_POINT_NOT_FOUND;
import kr.hhplus.ecommerce.domain.user.User;
import kr.hhplus.ecommerce.domain.user.UserFixture;
import kr.hhplus.ecommerce.domain.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserPointServiceTest {
    @InjectMocks
    private UserPointService service;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserPointRepository userPointRepository;
    @Mock
    private UserPointHistoryRepository userPointHistoryRepository;
    
    @Nested
    @DisplayName("충전 테스트")
    class ChargeTest {
        @Test
        void 성공() {
            // given
            User user = new UserFixture().create();
            UserPoint userPoint = new UserPointFixture().setUserId(user.id()).create();
            UserPointCommand.Charge command = new UserPointCommand.Charge(user.id(), 1000);
            given(userRepository.findById(user.id())).willReturn(Optional.of(user));
            given(userPointRepository.findByUserId(user.id())).willReturn(Optional.of(userPoint));
            given(userPointRepository.save(any(UserPoint.class))).willReturn(userPoint);

            // when
            service.charge(command);

            // then
            verify(userRepository).findById(user.id());
            verify(userPointRepository).findByUserId(user.id());
            verify(userPointRepository).save(any(UserPoint.class));
            verify(userPointHistoryRepository).save(any(UserPointHistory.class));
        }

        @Test
        void 탈퇴한_유저이면_실패() {
            // given
            User user = new UserFixture().setWithdrawnAt(LocalDateTime.now()).create();
            UserPointCommand.Charge command = new UserPointCommand.Charge(user.id(), 1000);
            given(userRepository.findById(user.id())).willReturn(Optional.of(user));

            // when
            Throwable throwable = catchThrowable(() -> service.charge(command));

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
        void 유저_포인트가_존재하지_않으면_신규_생성후_정상_동작() {
            // given
            User user = new UserFixture().create();
            UserPoint userPoint = new UserPointFixture().setUserId(user.id()).create();
            UserPointCommand.Charge command = new UserPointCommand.Charge(user.id(), 1000);
            given(userRepository.findById(user.id())).willReturn(Optional.of(user));
            given(userPointRepository.findByUserId(user.id())).willReturn(Optional.empty());
            given(userPointRepository.save(any(UserPoint.class))).willReturn(userPoint);
            given(userPointHistoryRepository.save(any(UserPointHistory.class)))
                .willReturn(UserPointHistory.createChargeHistory(user.id(), command.amount()));

            // when
            service.charge(command);

            // then
            verify(userRepository).findById(user.id());
            verify(userPointRepository).findByUserId(user.id());
            verify(userPointRepository).save(any(UserPoint.class));
            verify(userPointHistoryRepository).save(any(UserPointHistory.class));
        }
    }

    @Nested
    @DisplayName("사용 테스트")
    class UseTest {
        @Test
        void 성공() {
            // given
            User user = new UserFixture().create();
            UserPoint userPoint = new UserPointFixture().setUserId(user.id()).create();
            UserPointCommand.Use command = new UserPointCommand.Use(user.id(), 1000);
            given(userRepository.findById(user.id())).willReturn(Optional.of(user));
            given(userPointRepository.findByUserId(user.id()))
                .willReturn(Optional.of(userPoint));
            given(userPointRepository.save(any(UserPoint.class)))
                .willReturn(userPoint);

            // when
            service.use(command);

            // then
            verify(userRepository).findById(user.id());
            verify(userPointRepository).findByUserId(user.id());
            verify(userPointRepository).save(any(UserPoint.class));
            verify(userPointHistoryRepository).save(any(UserPointHistory.class));
        }

        @Test
        void 탈퇴한_유저이면_실패() {
            // given
            User user = new UserFixture().setWithdrawnAt(LocalDateTime.now()).create();
            UserPointCommand.Use command = new UserPointCommand.Use(user.id(), 1000);
            given(userRepository.findById(user.id())).willReturn(Optional.of(user));

            // when
            Throwable throwable = catchThrowable(() -> service.use(command));

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
            User user = new UserFixture().create();
            UserPointCommand.Use command = new UserPointCommand.Use(user.id(), 1000);
            given(userRepository.findById(user.id())).willReturn(Optional.of(user));
            given(userPointRepository.findByUserId(user.id()))
                .willReturn(Optional.empty());

            // when
            Throwable throwable = catchThrowable(() -> service.use(command));

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
} 