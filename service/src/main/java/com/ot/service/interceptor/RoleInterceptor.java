package com.ot.service.interceptor;

import com.ot.service.config.ErrorCodeEnum;
import com.ot.service.exception.BadRequestException;
import com.ot.service.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 각 API 별 접근권한 처리를 담당합니다.
 */
@Slf4j
public class RoleInterceptor implements HandlerInterceptor {
    private static final String ROLE_MASTER = "MASTER";
    private static final String ROLE_DEVELOP = "DEVELOP";
    private final TokenProvider tokenProvider;

    public RoleInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (request.getRequestURI().contains("/api/board")) {
            if (!tokenProvider.isCheckRole(ROLE_MASTER) && !tokenProvider.isCheckRole(ROLE_DEVELOP)) {
                throw new BadRequestException(ErrorCodeEnum.INVALID_ROLE);
            }
        }

        if (request.getRequestURI().contains("/api/user")) {
            if (!tokenProvider.isCheckRole(ROLE_MASTER)) {
                throw new BadRequestException(ErrorCodeEnum.INVALID_ROLE);
            }
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
