package com.ot.service.exception;

import com.ot.service.config.ErrorCodeEnum;
import lombok.Getter;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = -4688025679056011205L;

    private static ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

    static {
        messageSource.setBasenames("message");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
    }

    @Getter
    private String errorCode;
    private String errorMessage = "";

    @Override
    public String getMessage() {
        return this.getMessage(new Locale("en"));
    }

    public String getMessage(Locale locale) {
        return this.errorMessage;
    }

    public BadRequestException(){
    }

    public BadRequestException(String errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public BadRequestException(ErrorCodeEnum e) {
        super();
        this.errorCode = e.getCode();
        this.errorMessage = messageSource.getMessage(e.getMessage(), null, "BadRequestException", new Locale("en"));
    }
}
