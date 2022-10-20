package com.ot.service.config;

import com.ot.service.exception.CustomAuthenticationEntryPoint;
import com.ot.service.jwt.CustomUserDetailService;
import com.ot.service.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정을 담당합니다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtFilter jwtFilter;
    private final CustomUserDetailService userDetailService;

    // 암호화 방법 객체 생성 @Bean은 서버 구동 시 자동으로 객체 생성됨
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 로그인 요청이 올 경우 실행되며, userDetailService를 활용하여 로그인 요청을 처리
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
    }

    /*
         AuthenticationManager는 Spring Boot 2.x부터는 자동등록 되지 않는다.
         따라서 외부로 표출해 주는 메소드를 강제로 호출하여 @Bean으로 등록해 주어야 한다.
         @Bean에 있는 name 속성은 명시적으로 이 메소드에서 생성된 클래스가 프로그램의 AuthenticationManager라는 뜻이다.
     */
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/authenticate", "/refresh-token").permitAll()
                .antMatchers("/swagger*/**", "/v3/api-docs/**", "/api/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .and()
                .sessionManagement()
                // 세션을 스프링시큐리티가 생성하지도 않고 기존것을 사용하지도 않음
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //  UsernamePasswordAuthenticationFilter를 통과하기 전에 jwtFilter를 지나도록 설정
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
    }
}
