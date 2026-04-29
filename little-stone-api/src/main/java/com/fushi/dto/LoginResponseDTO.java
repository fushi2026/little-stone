package com.fushi.dto;

import com.fushi.entity.Permission;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LoginResponseDTO {
    private String token;
    private String username;
    private String realName;
    private List<String> roles = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();
    private List<Permission> menuTree = new ArrayList<>();
}
