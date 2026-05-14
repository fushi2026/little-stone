package com.fushi.service;

import com.fushi.dto.auth.LoginRequestDTO;
import com.fushi.dto.auth.LoginResponseDTO;
import com.fushi.dto.auth.RefreshTokenResponseDTO;
import com.fushi.dto.auth.RegisterRequestDTO;
import com.fushi.entity.SysUser;
import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

public interface SysUserService {
    public LoginResponseDTO login(LoginRequestDTO requestDTO) throws Exception;

    public RefreshTokenResponseDTO refreshToken(String refreshToken) throws Exception;

    public void register(RegisterRequestDTO requestDTO);

    public Optional<SysUser> findByUsername(@NotBlank(message = "用户名不能为空") String username);
}
