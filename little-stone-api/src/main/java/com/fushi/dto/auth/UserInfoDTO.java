package com.fushi.dto.auth;

import lombok.Data;

import java.util.ArrayList;

@Data
public class UserInfoDTO {
    private String username;
    private String nickname;
    private String avatar;
    private ArrayList<String> roleList = new ArrayList<>();
}
