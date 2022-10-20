package com.ot.admin.controller;

import com.ot.service.dto.BaseResponse;
import com.ot.service.dto.user.request.UserPostDto;
import com.ot.service.dto.user.request.UserPutDto;
import com.ot.service.dto.user.request.UserRoleUpdateDto;
import com.ot.service.dto.user.response.UserDto;
import com.ot.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 회원에 대한 HTTP 요청 처리를 담당합니다.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "jwtapi")
@Tag(name = "회원관리 API")
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "회원 전체 리스트")
    public ResponseEntity<BaseResponse<?>> selectUserAll() {
        List<UserDto> result = userService.selectUserAll();
        BaseResponse<?> res = new BaseResponse.Builder<>(result, true, HttpStatus.OK).build();

        return ResponseEntity.ok().body(res);
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "회원 단일 조회")
    public ResponseEntity<BaseResponse<?>> selectUserById(@PathVariable(name = "id") Long id) {
        UserDto result = userService.selectUserById(id);
        BaseResponse<?> res = new BaseResponse.Builder<>(result, true, HttpStatus.OK).build();

        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/insert")
    @Operation(summary = "회원 등록")
    public ResponseEntity<BaseResponse<?>> insertUser(@RequestBody @Valid UserPostDto request) throws URISyntaxException, NoSuchAlgorithmException {
        UserDto result = userService.insertUser(request);
        BaseResponse<?> res = new BaseResponse.Builder<>(result, true, HttpStatus.CREATED).build();

        return ResponseEntity.created(new URI("/api/user/" + result.getId())).body(res);
    }

    @PutMapping(value = "/update")
    @Operation(summary = "회원 데이터 수정")
    public ResponseEntity<BaseResponse<?>> updateUser(@RequestBody UserPutDto dto) {
        UserDto result = userService.updateUser(dto);
        BaseResponse<?> res = new BaseResponse.Builder<>(result, true, HttpStatus.OK).build();

        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "회원 데이터 삭제")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") Long id) {
        userService.deleteUserById(id);

        return ResponseEntity.noContent().build();
    }

    // 권한 변경 (MASTER 계정만 다른 계정 권한 변경가능, 본인권한 수정 X)
    @PutMapping(value = "/role")
    @Operation(summary = "회원 권한 수정")
    public ResponseEntity<BaseResponse<?>> updateUserRole(@RequestBody UserRoleUpdateDto dto) {
        UserDto result = userService.updateUserRole(dto);
        BaseResponse<?> res = new BaseResponse.Builder<>(result, true, HttpStatus.OK).build();

        return ResponseEntity.ok().body(res);
    }
}
