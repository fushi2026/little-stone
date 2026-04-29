package com.fushi.service.impl;

import com.fushi.dto.LoginRequestDTO;
import com.fushi.dto.LoginResponseDTO;
import com.fushi.entity.User;
import com.fushi.security.model.LoginUser;
import com.fushi.security.util.JwtTokenUtil;
import com.fushi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtUtils;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        //1、spring security 认证
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        //2、从认证结果里拿用户信息
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        User user = loginUser.getUser();

        //3、用户是否禁用
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }

        String token = jwtUtils.generateToken(user.getUsername());

        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setToken(token);
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setRoles(loginUser.getRoles());
        dto.setPermissions(loginUser.getPermissions());
        dto.setMenuTree(loginUser.getMenuTree());
        return dto;
    }
}
