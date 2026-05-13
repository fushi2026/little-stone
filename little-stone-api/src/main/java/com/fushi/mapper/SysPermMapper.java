package com.fushi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fushi.entity.SysPerm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysPermMapper extends BaseMapper<SysPerm> {
    public List<String> selectPermKeysByUserId(@Param("userId") Long userId);
}
