package com.ot.service.config;

import com.ot.service.filter.CustomServletWrappingFilter;
import com.ot.service.interceptor.LogInterceptor;
import com.ot.service.interceptor.RoleInterceptor;
import com.ot.service.jwt.TokenProvider;
import com.ot.service.service.LogService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LogService logService;

    private final TokenProvider tokenProvider;

    public WebConfig(LogService logService, TokenProvider tokenProvider) {
        this.logService = logService;
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public MessageSource validationMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/message");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(validationMessageSource());
        return bean;
    }

    @Bean
    public FilterRegistrationBean<CustomServletWrappingFilter> servletWrappingFilter() {
        final FilterRegistrationBean<CustomServletWrappingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CustomServletWrappingFilter());
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(new LogInterceptor(logService)).addPathPatterns("/api/**").addPathPatterns("/authenticate", "/refresh-token");
        registry.addInterceptor(new RoleInterceptor(tokenProvider)).addPathPatterns("/api/**");
    }
}
