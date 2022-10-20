package com.ot.admin.controller;

import com.ot.service.config.ErrorCodeEnum;
import com.ot.service.dto.BaseResponse;
import com.ot.service.dto.Login.LoginDto;
import com.ot.service.dto.Login.TokenDto;
import com.ot.service.exception.BadRequestException;
import com.ot.service.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "로그인 API")
public class LoginController {

    /*
        SecurityContextHolder는 Authentication을 담고 있는 곳이며
        실제 Authentication을 만들고 인증을 처리하는 인터페이스는 AuthenticationManager이다.
        AuthenticationManager는 authenticate라는 메서드만을 가지며
        여기서 authentication은 유저가 입력한 username, password 등의 인증정보를 담고 있다.
     */
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final StringRedisTemplate stringRedisTemplate;

    @PostMapping("/authenticate")
    public ResponseEntity<BaseResponse<?>> generateToken(@RequestBody LoginDto loginDto) {

        TokenDto result;

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

            result = TokenDto.builder()
                    .accessToken(tokenProvider.generateToken(loginDto.getUsername()))
                    .refreshToken(tokenProvider.generateRefreshToken(loginDto.getUsername()))
                    .build();
        } catch (Exception ex) {
            throw new BadRequestException(ErrorCodeEnum.WRONG_PASSWORD);
        }

        stringRedisTemplate.opsForValue().set(loginDto.getUsername(), result.getAccessToken(), 30, TimeUnit.MINUTES);
        stringRedisTemplate.opsForValue().set(loginDto.getUsername() + "_refresh", result.getRefreshToken(), 5, TimeUnit.HOURS);

        BaseResponse<?> res = new BaseResponse.Builder<>(result, true, HttpStatus.OK).build();
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<BaseResponse<?>> getRefreshToken(@RequestHeader(name = "Refresh-Token") String refreshToken) {

        try {
            if (tokenProvider.validateRefreshToken(refreshToken)) {
                String username = tokenProvider.extractUsername(refreshToken);
                TokenDto result = TokenDto.builder()
                        .accessToken(tokenProvider.generateToken(username))
                        .refreshToken(tokenProvider.generateRefreshToken(username))
                        .build();

                //Redis 추가
                stringRedisTemplate.opsForValue().set(username, result.getAccessToken(), 30, TimeUnit.MINUTES);
                stringRedisTemplate.opsForValue().set(username + "_refresh", result.getRefreshToken(), 5, TimeUnit.HOURS);

                BaseResponse<?> res = new BaseResponse.Builder<>(result, true, HttpStatus.OK).build();
                return ResponseEntity.ok().body(res);

            } else {
                throw new AuthenticationServiceException(ErrorCodeEnum.EXPIRED_REFRESH_TOKEN.getMessage());
            }
        } catch (Exception ex) {
            throw new AuthenticationServiceException(ErrorCodeEnum.EXPIRED_REFRESH_TOKEN.getMessage(), ex);
        }
    }
}
