package com.fushi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task")
public class Task {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long processInstanceId;
    private String nodeName;
    private String nodeType;
    private Long assigneeId;
    private Long creatorId;
    private Integer status;
    private String comment;
    private String action;
    private LocalDateTime createTime;
    private LocalDateTime completeTime;
    private LocalDateTime updateTime;
}