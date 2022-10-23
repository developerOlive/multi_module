package com.ot.service.dto.user.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserRoleUpdateDto {

    @Schema(description = "ID")
    private Long id;

    private List<String> userRoles;

    @JsonIgnore
    private String roles;

    @Schema(description = "수정자", example = "ID")
    @JsonIgnore
    private Long updUserSeq;
}
