package com.fushi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fushi.dto.form.ProcessDefinitionRequestDTO;
import com.fushi.entity.ProcessDefinition;

import java.util.Optional;

public interface ProcessDefinitionService extends IService<ProcessDefinition> {
    ProcessDefinition create(ProcessDefinitionRequestDTO requestDTO, Long creatorId);
    
    ProcessDefinition update(Long id, ProcessDefinitionRequestDTO requestDTO);
    
    void delete(Long id);
    
    Optional<ProcessDefinition> findByCode(String code);
}