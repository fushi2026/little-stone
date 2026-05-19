package com.fushi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fushi.dto.form.TaskOperateRequestDTO;
import com.fushi.entity.Task;

import java.util.List;

public interface TaskService extends IService<Task> {
    Task create(Long processInstanceId, String nodeName, String nodeType, Long assigneeId, Long creatorId);
    
    Task complete(TaskOperateRequestDTO requestDTO, Long operatorId);
    
    List<Task> findTodoTasks(Long userId);
    
    List<Task> findByProcessInstanceId(Long processInstanceId);
    
    void createTasksForNode(Long processInstanceId, String nodeName, Long assigneeId);
}