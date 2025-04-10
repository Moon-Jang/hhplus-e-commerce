package kr.hhplus.ecommerce.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class IssuedCouponServiceTest {
    @InjectMocks
    private IssuedCouponService service;
    @Mock
    private IssuedCouponRepository issuedCouponRepository;

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