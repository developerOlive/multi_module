package com.ot.service.dto.user.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "사용자 가입", name = "UserPostDto")
public class UserPostDto {

    @JsonIgnore
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

    private List<String> userRoles;

    @JsonIgnore
    private String roles;

    @Schema(description = "사용자 타입", example = "매체사, 광고사 등")
    private String userType;

    @Schema(description = "사용자 상태코드", example = "01")
    private String status;

    @JsonIgnore
    @Schema(description = "등록일시", example = "2022-09-01 12:12:12")
    private LocalDateTime regDate;

    @JsonIgnore
    @Schema(description = "등록자", example = "ID")
    private Long regUserSeq;

    @JsonIgnore
    @Schema(description = "수정일시", example = "2022-09-01 12:12:12")
    private LocalDateTime updateDate;

    @JsonIgnore
    @Schema(description = "수정자", example = "ID")
    private Long updUserSeq;

    @JsonIgnore
    @Schema(description = "마지막 로그인 일시", example = "2022-09-01 11:11:11")
    private LocalDateTime lastLoginDate;
}
