package com.fushi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fushi.entity.ProcessInstance;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProcessInstanceMapper extends BaseMapper<ProcessInstance> {
}