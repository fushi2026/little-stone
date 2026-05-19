package com.fushi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("process_instance")
public class ProcessInstance {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long processDefinitionId;
    private String businessKey;
    private Integer status;
    private String currentNode;
    private Long initiatorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}