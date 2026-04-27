package com.fushi.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fushi.entity.Permission;
import com.fushi.mapper.PermissionMapper;
import com.fushi.mapper.RolePermissionMapper;
import com.fushi.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<String> selectPermKeysByRoleIds(List<Long> roleIds) {
        if(roleIds.isEmpty()) {
            return List.of();
        }

        List<Long> permIds = rolePermissionMapper.selectPermIdsByRoleIds(roleIds);
        if(permIds.isEmpty()) {
            return List.of();
        }

        LambdaQueryWrapper<Permission> permQueryWrapper = new LambdaQueryWrapper<Permission>()
                .select(Permission::getPermKey)
                .in(Permission::getId, permIds)
                .eq(Permission::getStatus, 1);

        List<Permission> permList = permissionMapper.selectList(permQueryWrapper);

        return permList.stream().map(Permission::getPermKey).toList();
    }

}
