package com.ot.service.aop;

import com.ot.service.config.ErrorCodeEnum;
import com.ot.service.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor
@Transactional
public class SessionAspect {
    private final StringRedisTemplate stringRedisTemplate;
    @Around("within(com.ot.service.service..*)")
    public Object doAroundToken(final ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        String userToken = request.getHeader("Authorization");
        if (userToken != null) {
            String redisKey = SecurityContextHolder.getContext().getAuthentication().getName();
            String redisToken = stringRedisTemplate.opsForValue().get(redisKey);

            // [Redis의 토큰값]과 [사용자 토큰값]을 비교하여 일치하지 않는다면 에러 예외 처리
            if (redisToken != null && !userToken.substring(7).equals(redisToken)) {
                throw new BadRequestException(ErrorCodeEnum.AUTHENTICATE_INVALID_ERROR);
            }
        }

        return joinPoint.proceed();
    }
}
