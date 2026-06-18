package com.fushi.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 立项管理响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectLxResponseDTO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 项目编号
     */
    private String projectCode;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 项目类型
     */
    private String projectType;
    
    /**
     * 项目负责人ID
     */
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
     * 项目状态(0草稿,1待审批,2已立项,3已驳回,4已取消)
     */
    private Integer status;
    
    /**
     * 优先级(0低,1中,2高,3紧急)
     */
    private Integer priority;
    
    /**
     * 附件路径(JSON格式)
     */
    private String attachments;
    
    /**
     * 审批意见
     */
    private String approvalComment;
    
    /**
     * 审批人ID
     */
    private Long approverId;
    
    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;
    
    /**
     * 创建人ID
     */
    private Long creatorId;
    
    /**
     * 创建人姓名
     */
    private String creatorName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 优先级描述
     */
    private String priorityDesc;
}
