package com.fushi.dto.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class FormDefinitionRequestDTO {
    @NotBlank(message = "表单名称不能为空")
    private String name;
    
    @NotBlank(message = "表单编码不能为空")
    private String code;
    
    private String description;
    
    private List<FormFieldDTO> fields;
    
    private Object layoutConfig;
    
    private Integer status;
}