package com.fushi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fushi.entity.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysMenuMapper extends BaseMapper<SysMenu> {
    public List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);
}
