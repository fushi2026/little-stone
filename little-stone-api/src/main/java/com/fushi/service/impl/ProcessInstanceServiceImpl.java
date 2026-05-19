package com.fushi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fushi.entity.ProcessDefinition;
import com.fushi.entity.ProcessInstance;
import com.fushi.mapper.ProcessInstanceMapper;
import com.fushi.service.ProcessDefinitionService;
import com.fushi.service.ProcessInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProcessInstanceServiceImpl extends ServiceImpl<ProcessInstanceMapper, ProcessInstance> implements ProcessInstanceService {

    private final ProcessDefinitionService processDefinitionService;

    @Override
    public ProcessInstance start(Long processDefinitionId, String businessKey, Long initiatorId) {
        ProcessDefinition definition = processDefinitionService.getById(processDefinitionId);
        if (definition == null) {
            throw new RuntimeException("流程定义不存在");
        }

        ProcessInstance instance = new ProcessInstance();
        instance.setProcessDefinitionId(processDefinitionId);
        instance.setBusinessKey(businessKey);
        instance.setStatus(1);
        instance.setInitiatorId(initiatorId);
        instance.setStartTime(LocalDateTime.now());
        instance.setCreateTime(LocalDateTime.now());
        instance.setUpdateTime(LocalDateTime.now());
        save(instance);
        return instance;
    }

    @Override
    public void complete(Long id) {
        ProcessInstance instance = getById(id);
        if (instance == null) {
            throw new RuntimeException("流程实例不存在");
        }
        instance.setStatus(2);
        instance.setEndTime(LocalDateTime.now());
        updateById(instance);
    }

    @Override
    public void updateCurrentNode(Long id, String nodeName) {
        ProcessInstance instance = getById(id);
        if (instance == null) {
            throw new RuntimeException("流程实例不存在");
        }
        instance.setCurrentNode(nodeName);
        instance.setUpdateTime(LocalDateTime.now());
        updateById(instance);
    }
}