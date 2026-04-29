package com.fushi.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fushi.entity.Permission;
import com.fushi.entity.User;
import com.fushi.mapper.PermissionMapper;
import com.fushi.mapper.RoleMapper;
import com.fushi.mapper.UserMapper;
import com.fushi.security.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
    private PermissionMapper permissionMapper;


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

        //2、查询用户角色
        List<String> roleNames = roleMapper.selectRoleNamesByUserId(user.getId());

        //3、查询用户按钮权限标识
        List<String> permKeys = permissionMapper.selectPermKeysByUserId(user.getId());

        //用户菜单列表，构建树形对象
        List<Permission> permissions = permissionMapper.selectMenusByUserId(user.getId());
        List<Permission> menuTree = buildMenuTree(permissions);


        List<GrantedAuthority> authorities = new ArrayList<>();

        for(String roleName: roleNames) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
        };

        for(String permKey: permKeys) {
            authorities.add(new SimpleGrantedAuthority(permKey));
        };

        return new LoginUser(user, roleNames, permKeys, menuTree, authorities);
    }

    private List<Permission> buildMenuTree(List<Permission> list) {
        List<Permission> menuTree = new ArrayList<>();
        for(Permission p: list) {
            if (p.getParentId() == 0) {
                menuTree.add(findChildren(p, list));
            }
        }
        return menuTree;
    }

    private Permission findChildren(Permission parent, List<Permission> list) {
        for(Permission p: list) {
            if(p.getParentId().equals(parent.getId())) {
                parent.getChildren().add(findChildren(p, list));
            }
        }
        return parent;
    }


}
