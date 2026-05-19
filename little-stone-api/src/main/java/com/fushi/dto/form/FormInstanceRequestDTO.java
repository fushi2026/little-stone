package com.fushi.dto.form;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class FormInstanceRequestDTO {
    @NotNull(message = "表单定义ID不能为空")
    private Long formDefinitionId;
    
    private Map<String, Object> formData;
    
    private String businessKey;
}