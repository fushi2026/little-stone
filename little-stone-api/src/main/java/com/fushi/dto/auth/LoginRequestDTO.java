package com.fushi.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "加密后的密码不能为空")
    private String encryptedPassword;
    @NotBlank(message = "nonce不能为空")
    private String nonce;
    @NotBlank(message = "设备指纹不能为空")
    private String deviceFingerprint;
}