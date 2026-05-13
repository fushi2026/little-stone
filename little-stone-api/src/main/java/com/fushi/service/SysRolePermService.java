package com.fushi.service;

import java.util.List;

public interface SysRolePermService {
    List<String> selectPermKeysByRoleIds(List<Long> roleIds);
}
