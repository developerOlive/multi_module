package com.ot.service.jwt;

import com.ot.service.dto.user.response.UserDto;
import com.ot.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/*
    DB에서 사용자 정보를 가져오는 역할을 합니다.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        UserDto userDto = userMapper.getUserByUserId(userId);
        List<String> roles = List.of(userDto.getRoles().split(","));
        List<GrantedAuthority> grantedAuthorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new CustomUser(userDto.getId(), userDto.getUserId(), userDto.getPassword(), grantedAuthorities);
        //return new CustomUser(200L,"userId@naver.com", "password", null);
    }
}
