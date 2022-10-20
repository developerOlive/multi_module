package com.ot.service.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ot.service.config.ErrorCodeEnum;
import com.ot.service.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/*
    인증 또는 인가에 실패한 경우 예외처리를 담당합니다.
 */
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

    static {
        messageSource.setBasenames("message");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
    }
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        ObjectMapper objectMapper = new ObjectMapper();

        BaseResponse<?> result = new BaseResponse
                .Builder<>(
                false,
                ErrorCodeEnum.AUTHENTICATE_INVALID_ERROR.getCode(),
                messageSource.getMessage(ErrorCodeEnum.ARGUMENT_TEST.getMessage(), new String[]{"Authorized", "Invalid"}, null))
                .build();

        result.setStatus(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK.value());
        String json = objectMapper.writeValueAsString(result);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    }
}
