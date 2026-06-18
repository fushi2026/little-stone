package com.fushi.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 立项管理请求 DTO
 */
@Data
public class ProjectLxRequestDTO {
    
    /**
     * 项目ID（更新时必填）
     */
    private Long id;
    
    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    private String projectName;
    
    /**
     * 项目类型
     */
    @NotBlank(message = "项目类型不能为空")
    private String projectType;
    
    /**
     * 项目负责人ID
     */
    @NotNull(message = "项目负责人不能为空")
    private Long managerId;
    
    /**
     * 项目负责人姓名
     */
    private String managerName;
    
    /**
     * 项目预算
     */
    private BigDecimal budget;
    
    /**
     * 项目开始日期
     */
    private LocalDateTime startDate;
    
    /**
     * 项目结束日期
     */
    private LocalDateTime endDate;
    
    /**
     * 客户名称
     */
    private String customerName;
    
    /**
     * 客户联系人
     */
    private String contactPerson;
    
    /**
     * 联系电话
     */
    private String contactPhone;
    
    /**
     * 项目描述
     */
    private String description;
    
    /**
     * 优先级(0低,1中,2高,3紧急)
     */
    private Integer priority;
    
    /**
     * 附件路径(JSON格式)
     */
    private String attachments;
}
