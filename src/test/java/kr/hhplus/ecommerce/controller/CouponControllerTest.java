package kr.hhplus.ecommerce.controller;

import kr.hhplus.ecommerce.common.SpringBootTestContext;
import kr.hhplus.ecommerce.common.support.utils.JsonUtils;
import kr.hhplus.ecommerce.controller.request.CouponRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CouponControllerTest extends SpringBootTestContext {

    @Test
    @DisplayName("모든 쿠폰 목록 조회 성공 테스트")
    public void getAllCouponsSuccess() throws Exception {
        mockMvc.perform(get("/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").isNumber())
                .andExpect(jsonPath("$.data[0].name").isString())
                .andExpect(jsonPath("$.data[0].discountAmount").isNumber())
                .andExpect(jsonPath("$.data[0].issueStartTime").isString())
                .andExpect(jsonPath("$.data[0].issueStartTime").value(matchesPattern(ISO_DATE_PATTERN)))
                .andExpect(jsonPath("$.data[0].issueEndTime").isString())
                .andExpect(jsonPath("$.data[0].issueEndTime").value(matchesPattern(ISO_DATE_PATTERN)))
                .andExpect(jsonPath("$.data[0].maxQuantity").isNumber())
                .andExpect(jsonPath("$.data[0].currentQuantity").isNumber())
                .andExpect(jsonPath("$.data[0].discountAmount").isNumber())
                .andExpect(jsonPath("$.data[0].maxQuantity").isNumber())
                .andExpect(jsonPath("$.data[0].currentQuantity").isNumber());
    }

    @Test
    @DisplayName("사용자 쿠폰 발급 성공 테스트")
    public void issueCouponToUserSuccess() throws Exception {
        // given
        Long couponId = 1L;
        CouponRequest.Issue request = new CouponRequest.Issue(1L);
        String requestBody = JsonUtils.stringify(request);

        // when & then
        mockMvc.perform(post("/coupons/{couponId}/issue", couponId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.userId").isNumber())
                .andExpect(jsonPath("$.data.couponId").value(couponId.intValue()))
                .andExpect(jsonPath("$.data.couponName").isString())
                .andExpect(jsonPath("$.data.discountAmount").isNumber())
                .andExpect(jsonPath("$.data.expiryTime").isString())
                .andExpect(jsonPath("$.data.expiryTime").value(matchesPattern(ISO_DATE_PATTERN)))
                .andExpect(jsonPath("$.data.isUsed").isBoolean())
                .andExpect(jsonPath("$.data.createdAt").isString())
                .andExpect(jsonPath("$.data.createdAt").value(matchesPattern(ISO_DATE_PATTERN)))
                .andExpect(jsonPath("$.data.discountAmount", greaterThan(0)))
                .andExpect(jsonPath("$.data.isUsed", is(false)));
    }

    @Test
    @DisplayName("사용자 쿠폰 목록 조회 성공 테스트")
    public void getUserCouponsSuccess() throws Exception {
        // given
        Long userId = 1L;

        // when & then
        mockMvc.perform(get("/coupons/issued/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").isNumber())
                .andExpect(jsonPath("$.data[0].userId").value(userId.intValue()))
                .andExpect(jsonPath("$.data[0].couponId").isNumber())
                .andExpect(jsonPath("$.data[0].couponName").isString())
                .andExpect(jsonPath("$.data[0].discountAmount").isNumber())
                .andExpect(jsonPath("$.data[0].expiryTime").isString())
                .andExpect(jsonPath("$.data[0].expiryTime").value(matchesPattern(ISO_DATE_PATTERN)))
                .andExpect(jsonPath("$.data[0].isUsed").isBoolean())
                .andExpect(jsonPath("$.data[0].createdAt").isString())
                .andExpect(jsonPath("$.data[0].createdAt").value(matchesPattern(ISO_DATE_PATTERN)))
                .andExpect(jsonPath("$.data[0].discountAmount", greaterThan(0)));
    }
} 