package com.fushi.service;

import com.fushi.dto.LoginRequestDTO;
import com.fushi.dto.LoginResponseDTO;

public interface UserService {
    LoginResponseDTO login(LoginRequestDTO request);
}
