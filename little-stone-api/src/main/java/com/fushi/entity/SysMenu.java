package com.fushi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_menu")
public class SysMenu {
    @TableId(type= IdType.AUTO)
    private Long id;
    private String moduleCode;
    private Long parentId;
    private String menuName;
    private Integer menuType;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private Integer visible;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
