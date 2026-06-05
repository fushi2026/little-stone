package com.fushi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.fushi.dto.auth.MenuDTO;
import com.fushi.entity.SysMenu;
import com.fushi.mapper.SysMenuMapper;
import com.fushi.service.SysMenuService;
import com.fushi.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {
    private final SysMenuMapper sysMenuMapper;

    @Override
    public List<MenuDTO> getMenuList(Long userId) {
        List<SysMenu> sysMenus = sysMenuMapper.selectMenusByUserId(userId);

        List<MenuDTO> menuDTOList = sysMenus.stream()
                .map(m -> BeanUtil.copyProperties(m, MenuDTO.class))
                .toList();

        for (MenuDTO dto : menuDTOList) {
            setChildren(dto, menuDTOList);
        }

        List<MenuDTO> rootList = menuDTOList.stream()
                .filter(m -> m.getParentId() == null || m.getParentId() == 0)
                .toList();

        return rootList;
    }

    private void setChildren(MenuDTO menuDTO, List<MenuDTO> menuDTOList) {
        List<MenuDTO> children = menuDTOList.stream()
                .filter(m -> m.getParentId().equals(menuDTO.getId()))
                .toList();

        menuDTO.setChildren(children);

        for (MenuDTO child : children) {
            setChildren(child, menuDTOList);
        }
    }

}
