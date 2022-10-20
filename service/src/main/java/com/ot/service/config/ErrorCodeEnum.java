package com.ot.service.config;

public enum ErrorCodeEnum {
    INTERVAL_ERROR("E001", "err.itv"),
    DTO_INVALID_ERROR("E002", "err.div"),
    AUTHENTICATE_INVALID_ERROR("A001", "err.aie"),
    EXPIRED_REFRESH_TOKEN       ("A002","err.ert"),

    TITLE_NOT_NULL("B001", "err.tnn"),

    CONTENTS_NOT_NULL("B002", "err.cnn"),
    INVALID_BOARD_ID("B003", "err.ivbi"),
    INVALID_USER_ID("M001", "err.ivui"),
    DUPLICATE_USER_ID("M002", "err.dui"),
    INVALID_ROLE("M003", "err.ivr"),
    INVALID_REQUEST_USER       ("M004","err.ivru"),

    WRONG_PASSWORD ("M004", "err.wpw"),
    ARGUMENT_TEST       ("TEST","err.arg"),
    ;

    final private String code;
    final private String message;

    ErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
