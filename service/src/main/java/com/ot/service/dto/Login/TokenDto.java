package com.ot.service.dto.Login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "토큰 Dto")
@Builder
public class TokenDto implements Serializable {

    @Schema(required = true, description = "인증토큰")
    private String accessToken;

    @Schema(required = true, description = "재발행토큰")
    private String refreshToken;
}
