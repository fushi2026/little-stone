package com.fushi.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fushi.entity.User;
import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

public interface UserMapper extends BaseMapper<User> {
    default Optional<User> findByUsername(@NotBlank(message = "用户名不能为空") String username) {
        return Optional.ofNullable(selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username)));
    }
}
