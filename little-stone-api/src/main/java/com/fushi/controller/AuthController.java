package com.fushi.controller;

import com.fushi.common.enums.ResultCode;
import com.fushi.common.response.ApiResponse;
import com.fushi.dto.auth.LoginRequestDTO;
import com.fushi.dto.auth.LoginResponseDTO;
import com.fushi.dto.auth.RegisterRequestDTO;
import com.fushi.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final SysUserService sysUserService;

    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        try {
            LoginResponseDTO responseDTO = sysUserService.login(requestDTO);

            log.info("用户[{}]登录成功！", requestDTO.getUsername());
            return ApiResponse.success(responseDTO);
        } catch (Exception e) {
            log.error("用户[{}]登录异常！", requestDTO.getUsername(), e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), "用户登录失败");
        }
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody RegisterRequestDTO requestDTO) {
        try {
            //判断下用户名是否已存在
            if (sysUserService.findByUsername(requestDTO.getUsername()).isPresent()) {
                return ApiResponse.error(ResultCode.USER_EXIST.getCode(), "用户名已存在");
            }

            //注册用户
            sysUserService.register(requestDTO);

            log.info("用户[{}]注册成功", requestDTO.getUsername());
            return ApiResponse.success("用户注册成功");
        } catch (Exception e) {
            log.error("用户[{}]注册异常！", requestDTO.getUsername(), e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }
}
