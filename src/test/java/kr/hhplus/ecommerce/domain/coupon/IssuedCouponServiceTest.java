package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.common.exception.BadRequestException;
import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.user.User;
import kr.hhplus.ecommerce.domain.user.UserFixture;
import kr.hhplus.ecommerce.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static kr.hhplus.ecommerce.domain.common.DomainStatus.COUPON_NOT_FOUND;
import static kr.hhplus.ecommerce.domain.common.DomainStatus.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class IssuedCouponServiceTest {
    @InjectMocks
    private IssuedCouponService service;
    @Mock
    private IssuedCouponRepository issuedCouponRepository;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private UserRepository userRepository;

    @Nested
    @DisplayName("쿠폰 발급 테스트")
    class IssueTest {
        @Test
        void 쿠폰_발급_성공() {
            // given
            long userId = 1L;
            long couponId = 1L;
            CouponCommand.Issue command = new CouponCommand.Issue(userId, couponId);
            User user = new UserFixture().create();
            Coupon coupon = new CouponFixture().create();
            IssuedCoupon issuedCoupon = new IssuedCouponFixture().create();

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(couponRepository.findByIdWithLock(couponId)).willReturn(Optional.of(coupon));
            given(issuedCouponRepository.save(any(IssuedCoupon.class))).willReturn(issuedCoupon);

            // when
            IssuedCouponVo result = service.issue(command);

            // then
            verify(userRepository).findById(userId);
            verify(couponRepository).findByIdWithLock(couponId);
            verify(issuedCouponRepository).save(any(IssuedCoupon.class));
            assertThat(result).isNotNull();
        }

        @Test
        void 존재하지_않는_사용자인_경우_실패() {
            // given
            long userId = 1L;
            long couponId = 1L;
            CouponCommand.Issue command = new CouponCommand.Issue(userId, couponId);
            given(userRepository.findById(userId)).willReturn(Optional.empty());
            
            // when
            Throwable throwable = catchThrowable(() -> service.issue(command));
            
            // then
            assertThat(throwable).isInstanceOf(BadRequestException.class)
                                .hasFieldOrPropertyWithValue("status", USER_NOT_FOUND);
            verify(userRepository).findById(userId);
            verify(couponRepository, never()).findById(couponId);
            verify(issuedCouponRepository, never()).save(any(IssuedCoupon.class));
        }
        
        @Test
        void 존재하지_않는_쿠폰인_경우_실패() {
            // given
            long userId = 1L;
            long couponId = 1L;
            CouponCommand.Issue command = new CouponCommand.Issue(userId, couponId);
            User user = new UserFixture().create();
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(couponRepository.findByIdWithLock(couponId)).willReturn(Optional.empty());
            
            // when
            Throwable throwable = catchThrowable(() -> service.issue(command));
            
            // then
            assertThat(throwable).isInstanceOf(NotFoundException.class)
                                .hasFieldOrPropertyWithValue("status", COUPON_NOT_FOUND);
            verify(userRepository).findById(userId);
            verify(couponRepository).findByIdWithLock(couponId);
            verify(issuedCouponRepository, never()).save(any(IssuedCoupon.class));
        }
    }

    @Nested
    @DisplayName("쿠폰 사용 테스트")
    class UseCouponTest {
        @Test
        void 쿠폰_사용_성공() {
            // given
            long issuedCouponId = 1L;
            LocalDateTime now = LocalDateTime.now();
            IssuedCoupon issuedCoupon = new IssuedCouponFixture().create();
            given(issuedCouponRepository.findById(issuedCouponId)).willReturn(Optional.of(issuedCoupon));

            // when
            Throwable throwable = catchThrowable(() -> service.use(issuedCouponId));

            // then
            assertThat(throwable).isNull();
            verify(issuedCouponRepository).findById(issuedCouponId);
            verify(issuedCouponRepository).save(any(IssuedCoupon.class));
        }
    }
} 