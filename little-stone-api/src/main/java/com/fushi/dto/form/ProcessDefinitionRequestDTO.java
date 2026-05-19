package com.fushi.dto.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ProcessDefinitionRequestDTO {
    @NotBlank(message = "流程名称不能为空")
    private String name;
    
    @NotBlank(message = "流程编码不能为空")
    private String code;
    
    private String description;
    
    @NotNull(message = "表单定义ID不能为空")
    private Long formDefinitionId;
    
    private List<FlowNodeDTO> flowNodes;
    
    private Integer status;

    @Data
    public static class FlowNodeDTO {
        private String nodeId;
        private String nodeName;
        private String nodeType;
        private Long assigneeId;
        private String assigneeType;
        private List<String> nextNodes;
        private Boolean isStart;
        private Boolean isEnd;
        private Integer sortOrder;
    }
}