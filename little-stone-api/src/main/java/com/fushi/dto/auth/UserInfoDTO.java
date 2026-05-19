package com.fushi.dto.auth;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserInfoDTO {
    private String username;
    private String nickname;
    private String avatar;
    private List<String> roleList = new ArrayList<>();
}