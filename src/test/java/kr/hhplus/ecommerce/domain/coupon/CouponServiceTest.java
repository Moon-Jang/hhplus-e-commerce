package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.common.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.ecommerce.domain.common.DomainStatus.COUPON_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @Nested
    @DisplayName("쿠폰 상세 조회 테스트")
    class GetCouponDetailTest {
        @Test
        void 쿠폰_상세_조회_성공() {
            // given
            Coupon coupon = new CouponFixture().create();
            given(couponRepository.findById(coupon.id())).willReturn(Optional.of(coupon));

            // when
            CouponVo couponVo = couponService.getCouponDetail(1L);

            // then
            verify(couponRepository).findById(1L);
            assertThat(couponVo.id()).isEqualTo(coupon.id());
            assertThat(couponVo.name()).isEqualTo(coupon.name());
            assertThat(couponVo.discountAmount()).isEqualTo(coupon.discountAmount());
            assertThat(couponVo.issueStartTime()).isEqualTo(coupon.issueStartTime());
            assertThat(couponVo.issueEndTime()).isEqualTo(coupon.issueEndTime());
            assertThat(couponVo.maxQuantity()).isEqualTo(coupon.maxQuantity());
            assertThat(couponVo.issuedQuantity()).isEqualTo(coupon.issuedQuantity());
            assertThat(couponVo.expiryDays()).isEqualTo(coupon.expiryDays());
        }

        @Test
        void 쿠폰_상세_조회_실패_쿠폰_없음() {
            // given
            given(couponRepository.findById(anyLong())).willReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> couponService.getCouponDetail(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(COUPON_NOT_FOUND.getMessage());
        }
    }
} 