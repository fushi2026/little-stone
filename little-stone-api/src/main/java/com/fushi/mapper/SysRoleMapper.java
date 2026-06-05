package com.fushi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fushi.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper extends BaseMapper<SysRole> {
    default List<String> selectRoleNamesByRoleIds(@Param("roleIds") List<Long> roleIds) {
        if(roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }

        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<SysRole>()
                .select(SysRole::getRoleName)
                .in(SysRole::getId, roleIds)
                .eq(SysRole::getStatus, 1);

        List<SysRole> roles = selectList(queryWrapper);

        return roles.stream().map(SysRole::getRoleName).toList();
    }

    public List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    public List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

}
