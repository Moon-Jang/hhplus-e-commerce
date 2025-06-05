package kr.hhplus.ecommerce.interfaces.coupon.api;

import kr.hhplus.ecommerce.common.IntegrationTestContext;
import kr.hhplus.ecommerce.common.support.utils.JsonUtils;
import kr.hhplus.ecommerce.domain.coupon.CouponFixture;
import kr.hhplus.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponRepository;
import kr.hhplus.ecommerce.domain.user.UserFixture;
import kr.hhplus.ecommerce.domain.user.UserRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CouponE2ETest extends IntegrationTestContext {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    @Test
    void 선착순_쿠폰_발급_테스트() throws Exception {
        // given
        var startTime = System.currentTimeMillis();
        var user = userRepository.save(new UserFixture().setId(null).create());
        var coupon = couponRepository.save(new CouponFixture().setId(null).create());
        var request = new CouponRequest.Issue(user.id());

        // when
        mockMvc.perform(
                post("/v1/coupons/{couponId}/issue", coupon.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtils.stringify(request))
            )
            .andExpect(status().isOk());

        // then
        Awaitility.await()
            .pollInterval(100, TimeUnit.MILLISECONDS)
            .atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                var issuedCoupon = issuedCouponRepository.findByCouponIdAndUserId(coupon.id(), user.id());
                assertThat(issuedCoupon).isPresent();
            });
        var endTime = System.currentTimeMillis();
        System.out.println( "쿠폰 발급 시간: " + 436 + "ms");
    }
}
