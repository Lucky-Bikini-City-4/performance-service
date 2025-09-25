package com.dayaeyak.performance.interceptor;

import com.dayaeyak.performance.annotation.Authorize;
import com.dayaeyak.performance.common.enums.UserRole;
import com.dayaeyak.performance.common.exception.AuthorizationErrorCode;
import com.dayaeyak.performance.common.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Slf4j
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Authorize authorize = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Authorize.class); // method level

        if (authorize == null) {
            authorize = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Authorize.class); // class level
        }

        // 없으면 모든 권한 가능
        if (authorize == null) {
            return true;
        }

        // 명시 목적으로 bypass == true면 모든 권한 가능
        if (authorize.bypass()) {
            return true;
        }

        // 권한 목록이 비었으면 모든 권한 가능
        if (authorize.roles().length == 0) {
            return true;
        }

        String role = request.getHeader(USER_ROLE_HEADER);

        if (!StringUtils.hasText(role)) {
            throw new CustomException(AuthorizationErrorCode.INVALID_USER_ROLE);
        }

        UserRole userRole = UserRole.of(role);

        boolean isExists = Arrays.asList(authorize.roles())
                .contains(userRole);

        if (!isExists) {
            throw new CustomException(AuthorizationErrorCode.REQUEST_ACCESS_DENIED);
        }

        return true;
    }
}


