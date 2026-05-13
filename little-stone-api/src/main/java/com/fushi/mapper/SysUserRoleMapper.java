package com.fushi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fushi.entity.SysUserRole;

import java.util.List;
import java.util.stream.Collectors;

public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    default List<Long> selectRoleIdsByUserId(Long userId) {
        if(userId == null || userId < 0) {
            return List.of();
        }

        LambdaQueryWrapper<SysUserRole> query = new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId)
                .select(SysUserRole::getRoleId);

        List<SysUserRole> userRoles = selectList(query);

        return userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    }
}
