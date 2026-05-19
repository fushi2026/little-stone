package com.fushi.controller;

import com.fushi.common.enums.ResultCode;
import com.fushi.common.response.ApiResponse;
import com.fushi.dto.auth.*;
import com.fushi.service.SysUserService;
import com.fushi.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final SysUserService sysUserService;
    private final PasswordService passwordService;

    @GetMapping("/salt")
    public ApiResponse<SaltResponseDTO> getSalt(@Valid SaltRequestDTO requestDTO) {
        try {
            SaltResponseDTO responseDTO = passwordService.getSalt(requestDTO.getUsername());

            log.info("为用户[{}]生成密码盐成功", requestDTO.getUsername());
            return ApiResponse.success(responseDTO);
        } catch (Exception e) {
            log.error("为用户[{}]生成密码盐异常！", requestDTO.getUsername(), e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), "生成盐失败");
        }
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        try {
            LoginResponseDTO responseDTO = sysUserService.login(requestDTO);

            log.info("用户[{}]登录成功！设备指纹: {}", requestDTO.getUsername(), requestDTO.getDeviceFingerprint());
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

    @PostMapping("/refresh")
    public ApiResponse<RefreshTokenResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO requestDTO) {
        try {
            RefreshTokenResponseDTO responseDTO = sysUserService.refreshToken(requestDTO.getDeviceFingerprint());

            log.info("用户刷新 token 成功！设备指纹: {}", requestDTO.getDeviceFingerprint());
            return ApiResponse.success(responseDTO);
        } catch (Exception e) {
            log.error("刷新 token 异常！", e);
            return ApiResponse.error(ResultCode.UNAUTHORIZED.getCode(), e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(@Valid @RequestBody RefreshTokenRequestDTO requestDTO) {
        try {
            sysUserService.logout(requestDTO.getDeviceFingerprint());

            log.info("用户登出成功！设备指纹: {}", requestDTO.getDeviceFingerprint());
            return ApiResponse.success("登出成功");
        } catch (Exception e) {
            log.error("用户登出异常！", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }
}
