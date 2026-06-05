package com.fushi.service;

import com.fushi.dto.auth.MenuDTO;

import java.util.List;

public interface SysMenuService {
    public List<MenuDTO> getMenuList(Long userId);
}
