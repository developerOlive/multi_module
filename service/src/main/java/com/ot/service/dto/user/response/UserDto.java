package com.ot.service.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "사용자 데이터", name = "UserDto")
public class UserDto {

    @Schema(description = "seq", example = "1")
    private Long id;

    @Schema(description = "사용자 아이디", example = "userId@overtake.kr")
    private String userId;

    @Schema(description = "패스워드", example = "password")
    private String password;

    @Schema(description = "이름", example = "이름")
    private String name;

    @Schema(description = "휴대폰번호", example = "01012345678")
    private String phone;

    @Schema(description = "권한", example = "MASTER")
    private String roles;

    @Schema(description = "사용자 타입", example = "매체사, 광고사 등")
    private String userType;

    @Schema(description = "사용자 상태코드", example = "01")
    private String status;

    @Schema(description = "등록일시", example = "2022-09-01 12:12:12")
    private LocalDateTime regDate;

    @Schema(description = "마지막 로그인 일시", example = "2022-09-01 11:11:11")
    private LocalDateTime lastLoginDate;
}
