package com.ot.service.service;

import com.ot.service.config.ErrorCodeEnum;
import com.ot.service.dto.user.request.UserPostDto;
import com.ot.service.dto.user.request.UserPutDto;
import com.ot.service.dto.user.request.UserRoleUpdateDto;
import com.ot.service.dto.user.response.UserDto;
import com.ot.service.exception.BadRequestException;
import com.ot.service.jwt.TokenProvider;
import com.ot.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 회원 관리
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    private final static String ROLE_MASTER = "MASTER";

    @Transactional(readOnly = true)
    public List<UserDto> selectUserAll() {

        return userMapper.selectUserAll();
    }

    @Transactional(readOnly = true)
    public UserDto selectUserById(Long id) {
        UserDto result = userMapper.selectUserById(id);
        if (result == null) {
            throw new BadRequestException(ErrorCodeEnum.INVALID_USER_ID);
        }

        return result;
    }

    @Transactional
    public UserDto insertUser(UserPostDto param) {

        // 중복가입 방지
        if (isDuplicateUser(param.getUserId())) {
            throw new BadRequestException(ErrorCodeEnum.DUPLICATE_USER_ID);
        }

        // 비밀번호 암호화
        param.setPassword(passwordEncoder.encode(param.getPassword()));

        // 권한, 타입 세팅
        param.setRoles(setRoleByRoleList(param.getUserRoles()));
        param.setUserType(param.getUserType());

        userMapper.insertUser(param);

        return selectUserById(param.getId());
    }


    public Boolean isDuplicateUser(String userId) {
        UserDto user = userMapper.getUserByUserId(userId);
        if (user == null) {
            return false;
        } else return user.getStatus().equals("01");
    }

    public String setRoleByRoleList(List<String> roles) {
        StringBuilder result = new StringBuilder();
        for (String s : roles) {
            if (result.toString().equals("")) {
                result = new StringBuilder(s);
            } else {
                result.append(",").append(s);
            }
        }

        return result.toString();
    }

    public UserDto updateUser(UserPutDto param) {
        if (param.getId() == null || userMapper.selectUserById(param.getId()) == null) {
            throw new BadRequestException(ErrorCodeEnum.INVALID_USER_ID);
        }

        // 비밀번호 암호화
        param.setPassword(passwordEncoder.encode(param.getPassword()));

        param.setUpdUserSeq(tokenProvider.getUserByPrincipal().getId());

        userMapper.updateUser(param);

        return selectUserById(param.getId());
    }

    public void deleteUserById(Long id) {

        if (userMapper.selectUserById(id) == null) {
            throw new BadRequestException(ErrorCodeEnum.INVALID_USER_ID);
        }

        Map<String, Long> map = new HashMap<>();
        map.put("id", id);
        map.put("updUserSeq", tokenProvider.getUserByPrincipal().getId());

        userMapper.deleteUserById(map);
    }

    public UserDto updateUserRole(UserRoleUpdateDto param) {

        if (param.getId() == null || userMapper.selectUserById(param.getId()) == null) {
            throw new BadRequestException(ErrorCodeEnum.INVALID_USER_ID);
        }
        if (!tokenProvider.isCheckRole(ROLE_MASTER)) {
            throw new BadRequestException(ErrorCodeEnum.INVALID_ROLE);
        }
        if (tokenProvider.getUserByPrincipal().getId().equals(param.getId())) {
            throw new BadRequestException(ErrorCodeEnum.INVALID_REQUEST_USER);
        }

        //권한 규격 세팅
        param.setRoles(setRoleByRoleList(param.getUserRoles()));
        userMapper.updateUserRole(param);

        return selectUserById(param.getId());
    }
}
