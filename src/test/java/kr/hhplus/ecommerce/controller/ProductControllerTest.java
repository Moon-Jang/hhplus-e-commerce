package kr.hhplus.ecommerce.controller;

import kr.hhplus.ecommerce.common.SpringBootTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest extends SpringBootTestContext {

    @Test
    @DisplayName("상품 목록 조회 성공 테스트")
    public void getAllProductsSuccess() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").isNumber())
                .andExpect(jsonPath("$.data[0].name").isString())
                .andExpect(jsonPath("$.data[0].price").isNumber())
                .andExpect(jsonPath("$.data[0].quantity").isNumber())
                .andExpect(jsonPath("$.data[0].price", greaterThan(0)))
                .andExpect(jsonPath("$.data[0].quantity", greaterThanOrEqualTo(0)));
    }

    @Test
    @DisplayName("상품 상세 조회 성공 테스트")
    public void getProductByIdSuccess() throws Exception {
        // given
        Long productId = 1L;

        // when & then
        mockMvc.perform(get("/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(productId.intValue()))
                .andExpect(jsonPath("$.data.name").isString())
                .andExpect(jsonPath("$.data.price").isNumber())
                .andExpect(jsonPath("$.data.quantity").isNumber())
                .andExpect(jsonPath("$.data.description").isString())
                .andExpect(jsonPath("$.data.price", greaterThan(0)))
                .andExpect(jsonPath("$.data.quantity", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.description", not(emptyString())));
    }

    @Test
    @DisplayName("인기 상품 목록 조회 성공 테스트")
    public void getPopularProductsSuccess() throws Exception {
        mockMvc.perform(get("/products/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").isNumber())
                .andExpect(jsonPath("$.data[0].name").isString())
                .andExpect(jsonPath("$.data[0].price").isNumber())
                .andExpect(jsonPath("$.data[0].quantity").isNumber())
                .andExpect(jsonPath("$.data[0].soldCount").isNumber())
                .andExpect(jsonPath("$.data[0].price", greaterThan(0)))
                .andExpect(jsonPath("$.data[0].quantity", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data[0].soldCount", greaterThan(0)));
    }
} 