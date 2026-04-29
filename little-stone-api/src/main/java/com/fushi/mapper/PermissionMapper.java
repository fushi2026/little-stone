package com.fushi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fushi.entity.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {
    public List<String> selectPermKeysByUserId(@Param("userId") Long userId);

    public List<Permission> selectMenusByUserId(@Param("userId") Long userId);
}
