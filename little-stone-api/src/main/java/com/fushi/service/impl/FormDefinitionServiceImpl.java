package com.fushi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fushi.dto.form.FormDefinitionRequestDTO;
import com.fushi.entity.FormDefinition;
import com.fushi.mapper.FormDefinitionMapper;
import com.fushi.service.FormDefinitionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FormDefinitionServiceImpl extends ServiceImpl<FormDefinitionMapper, FormDefinition> implements FormDefinitionService {

    private final ObjectMapper objectMapper;

    @Override
    public FormDefinition create(FormDefinitionRequestDTO requestDTO, Long creatorId) {
        FormDefinition definition = new FormDefinition();
        definition.setName(requestDTO.getName());
        definition.setCode(requestDTO.getCode());
        definition.setDescription(requestDTO.getDescription());
        try {
            definition.setFields(objectMapper.writeValueAsString(requestDTO.getFields()));
            definition.setLayoutConfig(objectMapper.writeValueAsString(requestDTO.getLayoutConfig()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("表单字段序列化失败", e);
        }
        definition.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : 1);
        definition.setCreatorId(creatorId);
        definition.setCreateTime(LocalDateTime.now());
        definition.setUpdateTime(LocalDateTime.now());
        save(definition);
        return definition;
    }

    @Override
    public FormDefinition update(Long id, FormDefinitionRequestDTO requestDTO) {
        FormDefinition definition = getById(id);
        if (definition == null) {
            throw new RuntimeException("表单定义不存在");
        }
        definition.setName(requestDTO.getName());
        definition.setCode(requestDTO.getCode());
        definition.setDescription(requestDTO.getDescription());
        try {
            definition.setFields(objectMapper.writeValueAsString(requestDTO.getFields()));
            definition.setLayoutConfig(objectMapper.writeValueAsString(requestDTO.getLayoutConfig()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("表单字段序列化失败", e);
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
        FormDefinition definition = getById(id);
        if (definition == null) {
            throw new RuntimeException("表单定义不存在");
        }
        removeById(id);
    }

    @Override
    public Optional<FormDefinition> findByCode(String code) {
        QueryWrapper<FormDefinition> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code);
        return Optional.ofNullable(getOne(wrapper));
    }
}