package com.fushi.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDTO {
    @NotBlank(message = "设备指纹不能为空")
    private String deviceFingerprint;
}