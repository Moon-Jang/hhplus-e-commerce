package kr.hhplus.ecommerce.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.ecommerce.common.exception.UnauthorizedException;
import kr.hhplus.ecommerce.domain.user.UserService;
import kr.hhplus.ecommerce.domain.user.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static kr.hhplus.ecommerce.common.support.ApplicationStatus.UNAUTHORIZED;

@Component
@RequiredArgsConstructor
public class UserValidationInterceptor implements HandlerInterceptor {

    private static final String PATH_VARS = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {
        // PathVariable에서 userId 추출
        Optional<Long> userId = Optional.ofNullable(request.getAttribute(PATH_VARS))
            .filter(Map.class::isInstance)
            .map(Map.class::cast)
            .map(vars -> vars.get("userId"))
            .map(String.class::cast)
            .map(Long::valueOf);

        userId.ifPresent(id -> {
            Optional<UserVo> user = userService.findById(id);

            if (user.isEmpty() || user.get().withdrawnAt() != null) {
                throw new UnauthorizedException(UNAUTHORIZED);
            }
        });

        return true;
    }
}
