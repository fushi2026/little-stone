package com.fushi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_module")
public class SysModule {
    @TableId(type= IdType.AUTO)
    private Long id;
    private String moduleCode;
    private String moduleName;
    private Integer sort;
    private String icon;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
}
