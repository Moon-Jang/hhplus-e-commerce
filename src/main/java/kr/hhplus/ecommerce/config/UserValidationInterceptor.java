package kr.hhplus.ecommerce.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.ecommerce.common.exception.UnauthorizedException;
import kr.hhplus.ecommerce.domain.user.UserService;
import kr.hhplus.ecommerce.domain.user.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Optional;

import static kr.hhplus.ecommerce.application.common.ApplicationStatus.UNAUTHORIZED;

@Component
@RequiredArgsConstructor
public class UserValidationInterceptor implements HandlerInterceptor {
    private static final String PATH_VARS = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
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
