package com.ot.service.dto.Login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "로그인 Dto")
public class LoginDto implements Serializable {

    @Schema(required = true, description = "사용자 아이디", example = "userId@overtake.kr")
    private String username;

    @Schema(required = true, description = "비밀번호", example = "password")
    private String password;
}
