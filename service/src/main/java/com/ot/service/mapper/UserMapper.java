package com.ot.service.mapper;

import com.ot.service.dto.user.request.UserPostDto;
import com.ot.service.dto.user.request.UserPutDto;
import com.ot.service.dto.user.request.UserRoleUpdateDto;
import com.ot.service.dto.user.response.UserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    List<UserDto> selectUserAll();
    UserDto selectUserById(@Param("id") Long id);
    void insertUser(UserPostDto param);
    UserDto getUserByUserId(String userId);
    void updateUser(UserPutDto param);
    void deleteUserById(Map<String, Long> map);
    void updateUserRole(UserRoleUpdateDto param);
}
