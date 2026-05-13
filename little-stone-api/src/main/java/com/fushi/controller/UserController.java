package com.fushi.controller;

import cn.hutool.core.bean.BeanUtil;
import com.fushi.common.response.ApiResponse;
import com.fushi.dto.UserListDTO;
import com.fushi.entity.SysUser;
import com.fushi.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "用户管理", description = "")
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private SysUserMapper userMapper;

    @PreAuthorize("hasAuthority('sys:user:list')")
    @Operation(summary = "获取用户列表", description = "获取用户列表")
    @GetMapping("/list")
    public ApiResponse<List<UserListDTO>> findAll(){
        List<SysUser> list = userMapper.selectList(null);

        List<UserListDTO> listDTO = BeanUtil.copyToList(list, UserListDTO.class);

        return ApiResponse.success(listDTO);
    }



}
