package com.fushi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fushi.entity.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {
    default List<String> selectRoleNamesByRoleIds(@Param("roleIds") List<Long> roleIds) {
        if(roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }

        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<Role>()
                .select(Role::getRoleName)
                .in(Role::getId, roleIds)
                .eq(Role::getStatus, 1);

        List<Role> roles = selectList(queryWrapper);

        return roles.stream().map(Role::getRoleName).toList();
    }
}
