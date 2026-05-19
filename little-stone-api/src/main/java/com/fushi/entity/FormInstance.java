package com.fushi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("form_instance")
public class FormInstance {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long formDefinitionId;
    private String formData;
    private String businessKey;
    private Long processInstanceId;
    private Integer status;
    private Long submitterId;
    private LocalDateTime submitTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}