package com.fushi.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SaltRequestDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
}