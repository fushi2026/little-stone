package com.fushi.dto.auth;

import lombok.Data;

@Data
public class RefreshTokenResponseDTO {
    private String token;
    private String refreshToken;
}
