package com.ot.service.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ot.service.config.ErrorCodeEnum;
import com.ot.service.dto.board.request.PostBoard;
import com.ot.service.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor
@Transactional
public class RequestAspect {

    private final StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping) && args(postBoard, ..)")
    public Object doAroundAu(ProceedingJoinPoint joinPoint, PostBoard postBoard) throws Throwable {

        ObjectMapper objectMapper = new ObjectMapper();
        // 서블릿 리퀘스트에서 URI 데이터 조회
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String uri = request.getRequestURI();
        String redisKey = uri + objectMapper.writeValueAsString(postBoard);

        // redis 서버에서 해당 URI + 트랜잭션번호가 존재하는지 확인
        if (stringRedisTemplate.opsForValue().get(redisKey) != null) {
            // 존재한다면 Exception 발생
            throw new BadRequestException(ErrorCodeEnum.INTERVAL_ERROR);
        }

        Object obj = joinPoint.proceed();

        // URI+트랜잭션번호, 빈 Value, 타임아웃 시간 5, 타임유닛(규격) SECONDS --> redis 저장
        stringRedisTemplate.opsForValue().set(redisKey, "", 5, TimeUnit.SECONDS);


        return obj;
    }
}
