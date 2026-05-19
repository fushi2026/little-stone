package com.fushi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("process_definition")
public class ProcessDefinition {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String code;
    private String description;
    private String flowConfig;
    private Long formDefinitionId;
    private Integer status;
    private Long creatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}