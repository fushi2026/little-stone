package com.fushi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fushi.entity.SysUser;

import java.util.Optional;

public interface SysUserMapper extends BaseMapper<SysUser> {
    default Optional<SysUser> findByUsername(String username) {
        return Optional.ofNullable(selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)));
    }
}
