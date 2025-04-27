package kr.hhplus.ecommerce.interfaces;

import kr.hhplus.ecommerce.common.IntegrationTestContext;
import kr.hhplus.ecommerce.domain.user.UserService;
import kr.hhplus.ecommerce.domain.user.UserVo;
import kr.hhplus.ecommerce.interfaces.common.web.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(UserValidationInterceptorTest.TestController.class)
class UserValidationInterceptorTest extends IntegrationTestContext {
    private static final Long VALID_USER_ID = 1L;
    private static final Long INVALID_USER_ID = 999L;
    private static final Long WITHDRAWN_USER_ID = 2L;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        UserVo validUser = new UserVo(VALID_USER_ID, "Valid User", null);
        UserVo withdrawnUser = new UserVo(WITHDRAWN_USER_ID, "Withdrawn User", LocalDateTime.now());

        given(userService.findById(VALID_USER_ID)).willReturn(Optional.of(validUser));
        given(userService.findById(INVALID_USER_ID)).willReturn(Optional.empty());
        given(userService.findById(WITHDRAWN_USER_ID)).willReturn(Optional.of(withdrawnUser));
    }

    @Test
    @DisplayName("경로 변수에 유효한 userId가 있으면 검증 성공 후 200 OK 응답")
    void when_validUserIdInPath_then_validationSucceeds() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/test/users/{userId}", VALID_USER_ID));

        // then
        result.andExpect(status().isOk());
        verify(userService).findById(VALID_USER_ID);
    }

    @Test
    @DisplayName("경로 변수에 유효하지 않은 userId가 있으면 검증 실패 후 401 Unauthorized 응답")
    void when_invalidUserIdInPath_then_validationFailsWithUnauthorized() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/test/users/{userId}", INVALID_USER_ID));

        // then
        result.andExpect(status().isUnauthorized());
        verify(userService).findById(INVALID_USER_ID);
    }

    @Test
    @DisplayName("경로 변수에 탈퇴한 userId가 있으면 검증 실패 후 401 Unauthorized 응답")
    void when_withdrawnUserIdInPath_then_validationFailsWithUnauthorized() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/test/users/{userId}", WITHDRAWN_USER_ID));

        // then
        result.andExpect(status().isUnauthorized());
        verify(userService).findById(WITHDRAWN_USER_ID);
    }

    @Test
    @DisplayName("요청에 userId 정보가 없으면 검증 없이 200 OK 응답")
    void when_noUserId_then_noValidationAndSucceeds() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/test/no-user"));

        // then
        result.andExpect(status().isOk());
        verify(userService, never()).findById(anyLong());
    }

    @RestController
    static class TestController {
        @GetMapping("/test/users/{userId}")
        public ApiResponse<Void> getUserByPath(@PathVariable Long userId) {
            return ApiResponse.success();
        }

        @GetMapping("/test/no-user")
        public ApiResponse<Void> noUser() {
            return ApiResponse.success();
        }
    }
} 