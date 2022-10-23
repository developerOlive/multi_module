package com.ot.service.dto.user.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "사용자 정보 수정", name = "UserPutDto")
public class UserPutDto {

    @Schema(description = "seq", example = "1")
    private Long id;

    @Schema(description = "패스워드", example = "password")
    private String password;

    @Schema(description = "이름", example = "이름")
    private String name;

    @Schema(description = "휴대폰번호", example = "01012345678")
    private String phone;

    @JsonIgnore
    @Schema(description = "수정자", example = "ID")
    private Long updUserSeq;

    @JsonIgnore
    @Schema(description = "수정일시", example = "2022-09-01 12:12:12")
    private LocalDateTime updateDate;
}
