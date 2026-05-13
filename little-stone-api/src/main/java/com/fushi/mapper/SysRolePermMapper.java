package com.fushi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fushi.entity.SysRolePerm;

import java.util.List;

public interface SysRolePermMapper extends BaseMapper<SysRolePerm> {
    default List<Long> selectPermIdsByRoleIds(List<Long> roleIds) {
        if(roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }

        LambdaQueryWrapper<SysRolePerm> queryWrapper = new LambdaQueryWrapper<SysRolePerm>()
                .select(SysRolePerm::getPermId)
                .in(SysRolePerm::getRoleId, roleIds);

        List<SysRolePerm> rpList = selectList(queryWrapper);

        return rpList.stream().map(SysRolePerm::getPermId).distinct().toList();
    }
}
