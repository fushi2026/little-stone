package com.fushi.dto.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskOperateRequestDTO {
    @NotNull(message = "任务ID不能为空")
    private Long taskId;
    
    @NotBlank(message = "操作类型不能为空")
    private String action;
    
    private String comment;
}