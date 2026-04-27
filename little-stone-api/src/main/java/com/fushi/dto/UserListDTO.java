package com.fushi.dto;

import lombok.Data;

@Data
public class UserListDTO {
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String createTime;
}
