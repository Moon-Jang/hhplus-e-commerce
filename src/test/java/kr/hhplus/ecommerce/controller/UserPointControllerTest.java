package kr.hhplus.ecommerce.controller;

import kr.hhplus.ecommerce.common.SpringBootTestContext;
import kr.hhplus.ecommerce.common.support.utils.JsonUtils;
import kr.hhplus.ecommerce.controller.request.UserPointRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserPointControllerTest extends SpringBootTestContext {

    @Test
    @DisplayName("사용자 포인트 조회 성공 테스트")
    public void getUserPointSuccess() throws Exception {
        // given
        Long userId = 1L;

        // when & then
        mockMvc.perform(get("/users/{userId}/point", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").isNumber())
            .andExpect(jsonPath("$.data.userId").value(userId.intValue()))
            .andExpect(jsonPath("$.data.amount").isNumber());
    }

    @Test
    @DisplayName("사용자 포인트 충전 성공 테스트")
    public void chargePointSuccess() throws Exception {
        // given
        Long userId = 1L;
        UserPointRequest.Charge request = new UserPointRequest.Charge(10000L);
        String requestBody = JsonUtils.stringify(request);

        // when & then
        mockMvc.perform(post("/users/{userId}/point/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").isNumber())
            .andExpect(jsonPath("$.data.userId").value(userId.intValue()))
            .andExpect(jsonPath("$.data.amount").isNumber());
    }
}