package com.fushi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fushi.entity.SysModule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysModuleMapper extends BaseMapper<SysModule> {
    public List<SysModule> selectModulesByUserId(@Param("userId") Long userId);
}
