package com.fushi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fushi.entity.RolePermission;

import java.util.List;

public interface RolePermissionMapper extends BaseMapper<RolePermission> {
    default List<Long> selectPermIdsByRoleIds(List<Long> roleIds) {
        if(roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }

        LambdaQueryWrapper<RolePermission> queryWrapper = new LambdaQueryWrapper<RolePermission>()
                .select(RolePermission::getPermId)
                .in(RolePermission::getRoleId, roleIds);

        List<RolePermission> rpList = selectList(queryWrapper);

        return rpList.stream().map(RolePermission::getPermId).distinct().toList();
    }
}
