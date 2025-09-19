package com.dayaeyak.performance.resolver;

import com.dayaeyak.performance.annotation.PassportHolder;
import com.dayaeyak.performance.common.dto.Passport;
import com.dayaeyak.performance.common.enums.UserRole;
import com.dayaeyak.performance.common.exception.AuthorizationErrorCode;
import com.dayaeyak.performance.common.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class PassportHolderArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PassportHolder.class)
                && parameter.getParameterType().equals(Passport.class);
    }

    @Override
    public Passport resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String id = request.getHeader(USER_ID_HEADER);
        String role = request.getHeader(USER_ROLE_HEADER);

        if (!StringUtils.hasText(id)) {
            throw new CustomException(AuthorizationErrorCode.INVALID_USER_ID);
        }

        if (!StringUtils.hasText(role)) {
            throw new CustomException(AuthorizationErrorCode.INVALID_USER_ROLE);
        }

        return new Passport(
                Long.valueOf(id),
                UserRole.of(role)
        );
    }
}
