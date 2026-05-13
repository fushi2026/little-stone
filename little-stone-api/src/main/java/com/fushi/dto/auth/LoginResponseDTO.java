package com.fushi.dto.auth;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LoginResponseDTO {
    private String token;
    private String refreshToken;

    private UserInfoDTO userInfo;

    private List<ModuleDTO> moduleList = new ArrayList<>();
    private List<MenuDTO> menuList = new ArrayList<>();
    private List<String> permList = new ArrayList<>();
}
