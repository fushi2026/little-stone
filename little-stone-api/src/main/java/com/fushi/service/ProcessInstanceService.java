package com.fushi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fushi.entity.ProcessInstance;

public interface ProcessInstanceService extends IService<ProcessInstance> {
    ProcessInstance start(Long processDefinitionId, String businessKey, Long initiatorId);
    
    void complete(Long id);
    
    void updateCurrentNode(Long id, String nodeName);
}