package com.fushi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fushi.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    @Select("SELECT * FROM task WHERE assignee_id = #{assigneeId} AND status = #{status}")
    List<Task> selectByAssigneeAndStatus(@Param("assigneeId") Long assigneeId, @Param("status") Integer status);

    @Select("SELECT * FROM task WHERE process_instance_id = #{processInstanceId} ORDER BY create_time DESC")
    List<Task> selectByProcessInstanceId(@Param("processInstanceId") Long processInstanceId);
}