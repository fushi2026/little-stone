package com.fushi.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fushi.entity.SysUser;
import com.fushi.mapper.SysPermMapper;
import com.fushi.mapper.SysRoleMapper;
import com.fushi.mapper.SysUserMapper;
import com.fushi.security.model.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysPermMapper sysPermMapper;

    @Override
    public @NonNull LoginUser loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        LoginUser loginUser = new LoginUser();

        //从数据库获取用户信息
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);

        if(sysUser == null) {
            log.error("用户认证时报：用户名[{}]不存在", username);
            throw new UsernameNotFoundException("用户不存在：" + username);
        }

        if(sysUser.getStatus() == 0) {
            log.warn("用户认证失败：用户名[{}]已被禁用", username);
            throw new UsernameNotFoundException("用户已被禁用，请联系管理员");
        }

        //userId, username, password
        loginUser.setUserId(sysUser.getId());
        loginUser.setUsername(sysUser.getUsername());
        loginUser.setPassword(sysUser.getPassword());

        // 权限
        List<GrantedAuthority> authorities = new ArrayList<>();

        List<String> permKeys = sysPermMapper.selectPermKeysByUserId(sysUser.getId());
        for(String permKey : permKeys) {
            authorities.add(new SimpleGrantedAuthority(permKey));
        }

        List<String> roleCodes = sysRoleMapper.selectRoleCodesByUserId(sysUser.getId());
        for (String roleCode : roleCodes) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleCode));
        }

        loginUser.setAuthorities(authorities);

        return loginUser;
    }

}
