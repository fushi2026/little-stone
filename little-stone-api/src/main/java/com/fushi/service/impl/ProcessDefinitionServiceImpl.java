package com.fushi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fushi.dto.form.ProcessDefinitionRequestDTO;
import com.fushi.entity.ProcessDefinition;
import com.fushi.mapper.ProcessDefinitionMapper;
import com.fushi.service.ProcessDefinitionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProcessDefinitionServiceImpl extends ServiceImpl<ProcessDefinitionMapper, ProcessDefinition> implements ProcessDefinitionService {

    private final ObjectMapper objectMapper;

    @Override
    public ProcessDefinition create(ProcessDefinitionRequestDTO requestDTO, Long creatorId) {
        ProcessDefinition definition = new ProcessDefinition();
        definition.setName(requestDTO.getName());
        definition.setCode(requestDTO.getCode());
        definition.setDescription(requestDTO.getDescription());
        definition.setFormDefinitionId(requestDTO.getFormDefinitionId());
        try {
            definition.setFlowConfig(objectMapper.writeValueAsString(requestDTO.getFlowNodes()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("流程配置序列化失败", e);
        }
        definition.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : 1);
        definition.setCreatorId(creatorId);
        definition.setCreateTime(LocalDateTime.now());
        definition.setUpdateTime(LocalDateTime.now());
        save(definition);
        return definition;
    }

    @Override
    public ProcessDefinition update(Long id, ProcessDefinitionRequestDTO requestDTO) {
        ProcessDefinition definition = getById(id);
        if (definition == null) {
            throw new RuntimeException("流程定义不存在");
        }
        definition.setName(requestDTO.getName());
        definition.setCode(requestDTO.getCode());
        definition.setDescription(requestDTO.getDescription());
        definition.setFormDefinitionId(requestDTO.getFormDefinitionId());
        try {
            definition.setFlowConfig(objectMapper.writeValueAsString(requestDTO.getFlowNodes()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("流程配置序列化失败", e);
        }
        if (requestDTO.getStatus() != null) {
            definition.setStatus(requestDTO.getStatus());
        }
        definition.setUpdateTime(LocalDateTime.now());
        updateById(definition);
        return definition;
    }

    @Override
    public void delete(Long id) {
        ProcessDefinition definition = getById(id);
        if (definition == null) {
            throw new RuntimeException("流程定义不存在");
        }
        removeById(id);
    }

    @Override
    public Optional<ProcessDefinition> findByCode(String code) {
        QueryWrapper<ProcessDefinition> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code);
        return Optional.ofNullable(getOne(wrapper));
    }
}