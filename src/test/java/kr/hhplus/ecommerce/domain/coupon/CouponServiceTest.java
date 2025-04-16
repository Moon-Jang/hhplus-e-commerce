package kr.hhplus.ecommerce.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {
    @InjectMocks
    private CouponService couponService;
    @Mock
    private CouponRepository couponRepository;
    
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