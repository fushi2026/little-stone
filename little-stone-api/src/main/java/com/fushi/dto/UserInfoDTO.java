package com.fushi.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class UserInfoDTO {
    private Long id;
    private String username;
    private ArrayList<String> roles;
    private ArrayList<String> permissions;
}
