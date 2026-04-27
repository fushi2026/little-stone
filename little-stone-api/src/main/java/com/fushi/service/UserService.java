package com.fushi.service;

import com.fushi.dto.LoginResponseDTO;

public interface UserService {
    LoginResponseDTO getLoginUserInfo(String username);
}
