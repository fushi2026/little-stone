package com.fushi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fushi.dto.form.FormDefinitionRequestDTO;
import com.fushi.entity.FormDefinition;

import java.util.Optional;

public interface FormDefinitionService extends IService<FormDefinition> {
    FormDefinition create(FormDefinitionRequestDTO requestDTO, Long creatorId);
    
    FormDefinition update(Long id, FormDefinitionRequestDTO requestDTO);
    
    void delete(Long id);
    
    Optional<FormDefinition> findByCode(String code);
}