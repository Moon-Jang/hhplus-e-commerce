package kr.hhplus.ecommerce.interfaces;

import kr.hhplus.ecommerce.common.IntegrationTestContext;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CorsFilterTest extends IntegrationTestContext {

    @Test
    void 일반_요청시_응답_헤더에_CORS_설정이_포함된다() throws Exception {
        mockMvc.perform(get("/")
                        .header("Origin", "http://example.com"))
                .andExpect(status().isNotFound()) // 루트 경로에 매핑된 핸들러가 없다고 가정, 404 예상
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(header().exists("Access-Control-Allow-Methods"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH"))
                .andExpect(header().exists("Access-Control-Allow-Headers"))
                .andExpect(header().string("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With"))
                .andExpect(header().exists("Access-Control-Allow-Credentials"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
                .andExpect(header().exists("Access-Control-Max-Age"))
                .andExpect(header().string("Access-Control-Max-Age", "3600"));
    }

    @Test
    void Preflight_요청시_OK_상태코드와_전체_CORS_헤더를_응답한다() throws Exception {
        mockMvc.perform(options("/")
                        .header("Origin", "http://example.com")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type, Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(header().exists("Access-Control-Allow-Methods"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH"))
                .andExpect(header().exists("Access-Control-Allow-Headers"))
                .andExpect(header().string("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With"))
                .andExpect(header().exists("Access-Control-Allow-Credentials"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
                .andExpect(header().exists("Access-Control-Max-Age"))
                .andExpect(header().string("Access-Control-Max-Age", "3600"));
    }
} 