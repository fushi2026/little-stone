package com.fushi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fushi.entity.UserRole;

import java.util.List;
import java.util.stream.Collectors;

public interface UserRoleMapper extends BaseMapper<UserRole> {
    default List<Long> selectRoleIdsByUserId(Long userId) {
        if(userId == null || userId < 0) {
            return List.of();
        }

        LambdaQueryWrapper<UserRole> query = new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
                .select(UserRole::getRoleId);

        List<UserRole> userRoles = selectList(query);

        return userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
    }
}
