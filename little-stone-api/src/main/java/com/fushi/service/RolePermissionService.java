package com.fushi.service;

import java.util.List;

public interface RolePermissionService {
    List<String> selectPermKeysByRoleIds(List<Long> roleIds);
}
