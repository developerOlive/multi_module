package com.ot.service.exception;

import com.ot.service.config.ErrorCodeEnum;
import com.ot.service.dto.BaseResponse;
import com.ot.service.dto.DtoValidErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class ExceptionTranslator {

    private static ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

    static {
        messageSource.setBasenames("message");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<List<DtoValidErrorResponse>>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        log.error("handleValidationException", ex);

        //MethodArgumentNotValidException 에서 넘어온 필드명, 에러메시지를 DTO Valid 리스트에 저장
        List<DtoValidErrorResponse> errorList = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();

            if (errorList.stream().noneMatch(e -> e.getParam().equals(fieldName))) {
                errorList.add(DtoValidErrorResponse.builder().param(fieldName).errorMessage(errorMessage).build());
            }
        });

        BaseResponse<List<DtoValidErrorResponse>> result = new BaseResponse<>();
        result.setIsSucceed(false);
        result.setStatus(HttpStatus.OK.value());
        result.setErrorCode(ErrorCodeEnum.DTO_INVALID_ERROR.getCode());
        result.setErrorMessage(messageSource.getMessage(ErrorCodeEnum.DTO_INVALID_ERROR.getMessage(), null, null));
        result.setResult(errorList);
        return ResponseEntity.badRequest().body(result);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseResponse<?>> handleBadRequestExceptions(BadRequestException ex) {

        log.error("handleBadRequestException", ex);

        BaseResponse<?> result = new BaseResponse
                .Builder<>(false, ex.getErrorCode(), ex.getMessage())
                .build();
        result.setStatus(HttpStatus.OK.value());
        return ResponseEntity.badRequest().body(result);
    }
}
