package com.fushi.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fushi.entity.User;
import com.fushi.mapper.RoleMapper;
import com.fushi.mapper.UserMapper;
import com.fushi.mapper.UserRoleMapper;
import com.fushi.service.RolePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RolePermissionService rolePermissionService;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        if(user == null) {
            log.error("用户认证时报：用户名[{}]不存在", username);
            throw new UsernameNotFoundException("用户不存在：" + username);
        }

        if(user.getStatus() == 0) {
            log.warn("用户认证失败：用户名[{}]已被禁用", username);
            throw new UsernameNotFoundException("用户已被禁用，请联系管理员");
        }

        // 获取用户角色
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(user.getId());
        if(CollectionUtils.isEmpty(roleIds)) {
            log.info("用户[{}]没有角色", username);
        }

        // 获取角色权限
        List<GrantedAuthority> authorities = new ArrayList<>();

        if(!CollectionUtils.isEmpty(roleIds)) {
            List<String> roleNames = roleMapper.selectRoleNamesByRoleIds(roleIds);
            roleNames.forEach(roleName -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
                log.debug("用户[{}]加载角色ROLE_{}", username, roleName);
            });

            List<String> permKeys = rolePermissionService.selectPermKeysByRoleIds(roleIds);
            permKeys.forEach(permKey -> {
                authorities.add(new SimpleGrantedAuthority(permKey));
                log.debug("用户[{}]加载权限{}", username, permKey);
            });
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == 1,
                true,
                true,
                true,
                authorities
        );
    }
}
