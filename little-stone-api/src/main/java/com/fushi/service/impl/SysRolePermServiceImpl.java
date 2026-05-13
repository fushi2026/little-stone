package com.fushi.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fushi.entity.SysPerm;
import com.fushi.mapper.SysPermMapper;
import com.fushi.mapper.SysRolePermMapper;
import com.fushi.service.SysRolePermService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRolePermServiceImpl implements SysRolePermService {
    private final SysRolePermMapper sysRolePermMapper;
    private final SysPermMapper sysPermMapper;

    @Override
    public List<String> selectPermKeysByRoleIds(List<Long> roleIds) {
        if(roleIds.isEmpty()) {
            return List.of();
        }

        List<Long> permIds = sysRolePermMapper.selectPermIdsByRoleIds(roleIds);
        if(permIds.isEmpty()) {
            return List.of();
        }

        List<SysPerm> permList = sysPermMapper.selectList(new LambdaQueryWrapper<SysPerm>()
                .select(SysPerm::getPermKey)
                .in(SysPerm::getId, permIds)
                .eq(SysPerm::getStatus, 1)
        );

        return permList.stream().map(SysPerm::getPermKey).toList();
    }

}
