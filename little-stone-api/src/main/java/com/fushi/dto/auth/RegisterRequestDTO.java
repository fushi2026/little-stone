package com.fushi.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "加密后的密码不能为空")
    private String encryptedPassword;
    @NotBlank(message = "nonce不能为空")
    private String nonce;
    private String nickname;
    private String email;
    private String phone;
}