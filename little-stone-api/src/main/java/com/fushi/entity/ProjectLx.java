package com.fushi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("project_lx")
public class ProjectLx {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String projectCode;
    private String projectName;
    private String projectType;
    private Long managerId;
    private String managerName;
    private BigDecimal budget;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String customerName;
    private String contactPerson;
    private String contactPhone;
    private String description;
    private Integer status;
    private Integer priority;
    private String attachments;
    private String approvalComment;
    private Long approverId;
    private LocalDateTime approvalTime;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
