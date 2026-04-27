package com.fushi.service.impl;

import com.fushi.dto.LoginResponseDTO;
import com.fushi.entity.User;
import com.fushi.mapper.RoleMapper;
import com.fushi.mapper.RolePermissionMapper;
import com.fushi.mapper.UserMapper;
import com.fushi.mapper.UserRoleMapper;
import com.fushi.security.util.JwtTokenUtil;
import com.fushi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public LoginResponseDTO getLoginUserInfo(String username) {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();

        Optional<User> optionalUser = userMapper.findByUsername(username);
        optionalUser.ifPresent(user -> {
            loginResponseDTO.setUsername(username);
            loginResponseDTO.setRealName(user.getRealName());
            loginResponseDTO.setToken(jwtTokenUtil.generateToken(username));

            //用户角色
            List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(user.getId());
            List<String> roleNames = roleMapper.selectRoleNamesByRoleIds(roleIds);
            loginResponseDTO.setRoles(roleNames);

            //用户权限

        });

        return loginResponseDTO;
    }
}
