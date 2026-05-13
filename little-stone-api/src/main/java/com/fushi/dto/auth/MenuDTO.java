package com.fushi.dto.auth;

import lombok.Data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Data
public class MenuDTO {
    private Long id;
    private Long parentId;
    private String menuName;
    private String path;
    private String component;
    private String icon;
    private Integer sort;

    private List<MenuDTO> children = new ArrayList<>();
}
