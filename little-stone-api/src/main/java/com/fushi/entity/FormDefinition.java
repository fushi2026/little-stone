package com.fushi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("form_definition")
public class FormDefinition {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String code;
    private String description;
    private String fields;
    private String layoutConfig;
    private Integer status;
    private Long creatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}