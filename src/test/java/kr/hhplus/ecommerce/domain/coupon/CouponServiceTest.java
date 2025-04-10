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

import java.util.List;
import java.util.Optional;

import static kr.hhplus.ecommerce.common.support.DomainStatus.COUPON_NOT_FOUND;
import static kr.hhplus.ecommerce.common.support.DomainStatus.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {
    @InjectMocks
    private CouponService couponService;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private IssuedCouponRepository issuedCouponRepository;
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
            given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));
            given(issuedCouponRepository.save(any(IssuedCoupon.class))).willReturn(issuedCoupon);

            // when
            couponService.issue(command);

            // then
            verify(userRepository).findById(userId);
            verify(couponRepository).findById(couponId);
            verify(issuedCouponRepository).save(any(IssuedCoupon.class));
        }

        @Test
        void 존재하지_않는_사용자인_경우_실패() {
            // given
            long userId = 1L;
            long couponId = 1L;
            CouponCommand.Issue command = new CouponCommand.Issue(userId, couponId);
            given(userRepository.findById(userId)).willReturn(Optional.empty());
            
            // when
            Throwable throwable = catchThrowable(() -> couponService.issue(command));
            
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
            given(couponRepository.findById(couponId)).willReturn(Optional.empty());
            
            // when
            Throwable throwable = catchThrowable(() -> couponService.issue(command));
            
            // then
            assertThat(throwable).isInstanceOf(NotFoundException.class)
                                .hasFieldOrPropertyWithValue("status", COUPON_NOT_FOUND);
            verify(userRepository).findById(userId);
            verify(couponRepository).findById(couponId);
            verify(issuedCouponRepository, never()).save(any(IssuedCoupon.class));
        }
    }

    @Nested
    @DisplayName("쿠폰 조회 테스트")
    class GetAvailableCouponsTest {
        @Test
        void 쿠폰_조회_성공() {
            // given
            Coupon coupon = new CouponFixture().create();
            given(couponRepository.findAvailableCoupons(any())).willReturn(List.of(coupon));

            // when
            List<CouponVo> coupons = couponService.getAvailableCoupons();

            // then
            verify(couponRepository).findAvailableCoupons(any());
            assertThat(coupons).hasSize(1);
            assertThat(coupons.get(0).id()).isEqualTo(coupon.id());
            assertThat(coupons.get(0).name()).isEqualTo(coupon.name());
            assertThat(coupons.get(0).discountAmount()).isEqualTo(coupon.discountAmount());
            assertThat(coupons.get(0).issueStartTime()).isEqualTo(coupon.issueStartTime());
            assertThat(coupons.get(0).issueEndTime()).isEqualTo(coupon.issueEndTime());
            assertThat(coupons.get(0).maxQuantity()).isEqualTo(coupon.maxQuantity());
            assertThat(coupons.get(0).issuedQuantity()).isEqualTo(coupon.issuedQuantity());
            assertThat(coupons.get(0).expiryDays()).isEqualTo(coupon.expiryDays());
        }
    }
} 