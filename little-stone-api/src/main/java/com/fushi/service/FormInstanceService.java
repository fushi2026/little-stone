package com.fushi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fushi.dto.form.FormInstanceRequestDTO;
import com.fushi.entity.FormInstance;

public interface FormInstanceService extends IService<FormInstance> {
    FormInstance create(FormInstanceRequestDTO requestDTO, Long submitterId);
    
    FormInstance submit(FormInstanceRequestDTO requestDTO, Long submitterId);
    
    FormInstance getById(Long id);
}