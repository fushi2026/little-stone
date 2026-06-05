package com.fushi.controller;

import com.fushi.common.response.ApiResponse;
import com.fushi.dto.auth.MenuDTO;
import com.fushi.service.SysMenuService;
import com.fushi.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "菜单管理", description = "")
@RestController
@RequestMapping("/api/menu")
@Slf4j
@RequiredArgsConstructor
public class MenuController {
    private final SysMenuService sysMenuService;

    @GetMapping("/getUserMenus")
    public ApiResponse<List<MenuDTO>> getUserMenus() {
        List<MenuDTO> list = sysMenuService.getMenuList(SecurityUtil.getUserId());
        return ApiResponse.success(list);
    }

}
