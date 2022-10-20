package com.ot.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@Data
@Schema(description = "DTO 유효성 에러 응답 결과")
@Builder
public class DtoValidErrorResponse implements Serializable {

    private String param;
    private String errorMessage;
}
