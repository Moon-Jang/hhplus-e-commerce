package kr.hhplus.ecommerce.controller;

import kr.hhplus.ecommerce.common.SpringBootTestContext;
import kr.hhplus.ecommerce.common.support.utils.JsonUtils;
import kr.hhplus.ecommerce.controller.request.OrderRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderControllerTest extends SpringBootTestContext {

    @Test
    @DisplayName("주문 생성 성공 테스트")
    public void createOrderSuccess() throws Exception {
        // given
        OrderRequest.Create request = new OrderRequest.Create(
            1L,
            List.of(
                new OrderRequest.Create.OrderItem(1L, 2),
                new OrderRequest.Create.OrderItem(2L, 1)
            ),
            Optional.of(3L)
        );
        
        String requestBody = JsonUtils.stringify(request);

        // when & then
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.orderId").isNumber())
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].productId").isNumber())
                .andExpect(jsonPath("$.data.items[0].productName").isString())
                .andExpect(jsonPath("$.data.items[0].price").isNumber())
                .andExpect(jsonPath("$.data.items[0].quantity").isNumber())
                .andExpect(jsonPath("$.data.items[0].amount").isNumber())
                .andExpect(jsonPath("$.data.totalAmount").isNumber())
                .andExpect(jsonPath("$.data.discountAmount").isNumber())
                .andExpect(jsonPath("$.data.finalAmount").isNumber())
                .andExpect(jsonPath("$.data.coupon.couponId").isNumber())
                .andExpect(jsonPath("$.data.coupon.name").isString())
                .andExpect(jsonPath("$.data.coupon.discountAmount").isNumber())
                .andExpect(jsonPath("$.data.orderStatus").isString())
                .andExpect(jsonPath("$.data.createdAt").isString())
                .andExpect(jsonPath("$.data.createdAt").value(matchesPattern(ISO_DATE_PATTERN)));
    }
    
    @Test
    @DisplayName("주문 결제 성공 테스트")
    public void payOrderSuccess() throws Exception {
        // given
        Long orderId = 1L;

        // when & then
        mockMvc.perform(post("/orders/{orderId}/pay", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                // OrderResponse.Order 타입 검증
            .andExpect(jsonPath("$.data.orderId").isNumber())
            .andExpect(jsonPath("$.data.userId").value(1))
            .andExpect(jsonPath("$.data.items").isArray())
            .andExpect(jsonPath("$.data.items[0].productId").isNumber())
            .andExpect(jsonPath("$.data.items[0].productName").isString())
            .andExpect(jsonPath("$.data.items[0].price").isNumber())
            .andExpect(jsonPath("$.data.items[0].quantity").isNumber())
            .andExpect(jsonPath("$.data.items[0].amount").isNumber())
            .andExpect(jsonPath("$.data.totalAmount").isNumber())
            .andExpect(jsonPath("$.data.discountAmount").isNumber())
            .andExpect(jsonPath("$.data.finalAmount").isNumber())
            .andExpect(jsonPath("$.data.coupon.couponId").isNumber())
            .andExpect(jsonPath("$.data.coupon.name").isString())
            .andExpect(jsonPath("$.data.coupon.discountAmount").isNumber())
            .andExpect(jsonPath("$.data.orderStatus").value("COMPLETED"))
            .andExpect(jsonPath("$.data.createdAt").isString())
            .andExpect(jsonPath("$.data.createdAt").value(matchesPattern(ISO_DATE_PATTERN)));
    }
} 