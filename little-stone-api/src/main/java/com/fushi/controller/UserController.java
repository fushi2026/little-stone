package com.fushi.controller;

import cn.hutool.core.bean.BeanUtil;
import com.fushi.common.enums.ResultCode;
import com.fushi.common.response.ApiResponse;
import com.fushi.dto.UserListDTO;
import com.fushi.dto.auth.ChangePasswordRequestDTO;
import com.fushi.entity.SysUser;
import com.fushi.mapper.SysUserMapper;
import com.fushi.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "用户管理", description = "")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final SysUserMapper sysUserMapper;
    private final SysUserService sysUserService;

    @PreAuthorize("hasAuthority('sys:user:list')")
    @Operation(summary = "获取用户列表", description = "获取用户列表")
    @GetMapping("/list")
    public ApiResponse<List<UserListDTO>> findAll(){
        List<SysUser> list = sysUserMapper.selectList(null);

        List<UserListDTO> listDTO = BeanUtil.copyToList(list, UserListDTO.class);

        return ApiResponse.success(listDTO);
    }

    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(@Valid @RequestBody ChangePasswordRequestDTO requestDTO) {
        try {
            sysUserService.changePassword(requestDTO);
            log.info("用户修改密码成功");
            return ApiResponse.success("密码修改成功");
        } catch (Exception e) {
            log.error("修改密码异常", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }
}