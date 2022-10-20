package com.ot.service.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;

import java.io.Serializable;


@Data
@Schema(description = "기본 응답 결과")
public class BaseResponse<T> implements Serializable {

    private static ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

    static {
        messageSource.setBasenames("messages/message");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
    }

    @Schema(required = true, description = "성공여부")
    private Boolean isSucceed;

    @Schema(required = true, description = "응답 코드")
    private Integer status;
    @Schema(required = true, description = "에러 코드", example = "ERR001")
    private String errorCode;
    @Schema(required = true, description = "에러 메세지", example = "필수 값 누락")
    private String errorMessage;
    @Schema(required = true, description = "결과 데이터")
    private T result;

    public BaseResponse() {

    }

    private BaseResponse(Builder<T> builder) {
        this.isSucceed = builder.isSucceed;
        this.status = builder.status;
        this.errorCode = builder.resultCode;
        this.errorMessage = builder.errorMessage;
        this.result = builder.result;
    }

    @JsonPropertyOrder(alphabetic = true)
    @Schema(description = "BaseResponse.Builder")
    public static class Builder<T> {
        @Schema(required = true, description = "성공여부")
        private Boolean isSucceed;
        @Schema(required = true, description = "응답 코드")
        private Integer status;
        @Schema(required = true, description = "결과 코드", example = "ERR001")
        private String resultCode;
        @Schema(required = true, description = "결과 메세지", example = "필수 값 누락")
        private String errorMessage;
        @Schema(required = true, description = "결과 데이터")
        private T result;

        public Builder(T result) {
            this.result = result;
        }

        public Builder(T result, String error) {
            this.result = result;
            this.resultCode = error;
        }

        public Builder(T result, Boolean isSucceed) {
            this.result = result;
            this.isSucceed = isSucceed;
            if (this.isSucceed) this.resultCode = "S0000";
        }

        public Builder(Boolean isSucceed, HttpStatus httpStatus) {
            this.isSucceed = isSucceed;
            this.status = httpStatus.value();
            if (this.isSucceed) this.resultCode = "S0000";
        }

        public Builder(T result, Boolean isSucceed, HttpStatus httpStatus) {
            this.result = result;
            this.isSucceed = isSucceed;
            this.status = httpStatus.value();
            if (this.isSucceed) this.resultCode = "S0000";
        }

        public Builder(String resultCode, String errorMessage) {
            this.resultCode = resultCode;
            this.errorMessage = errorMessage;
        }

        public Builder(Boolean isSucceed, String resultCode, String errorMessage) {
            this.isSucceed = isSucceed;
            this.resultCode = resultCode;
            this.errorMessage = errorMessage;
        }

        public Builder(Boolean isSucceed, HttpStatus httpstatus, String resultCode, String errorMessage) {
            this.isSucceed = isSucceed;
            this.status = httpstatus.value();;
            this.resultCode = resultCode;
            this.errorMessage = errorMessage;
        }

        public BaseResponse<?> build() {
            return new BaseResponse<>(this);
        }
    }
}
