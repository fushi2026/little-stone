package com.fushi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fushi.dto.auth.*;
import com.fushi.entity.SysMenu;
import com.fushi.entity.SysUser;
import com.fushi.mapper.SysMenuMapper;
import com.fushi.mapper.SysModuleMapper;
import com.fushi.mapper.SysRoleMapper;
import com.fushi.mapper.SysUserMapper;
import com.fushi.security.util.JwtTokenUtil;
import com.fushi.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.parsing.BeanEntry;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysModuleMapper sysModuleMapper;
    private final SysMenuMapper sysMenuMapper;

    @Override
    public LoginResponseDTO login(LoginRequestDTO requestDTO) throws Exception {
        //认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.getUsername(), requestDTO.getPassword())
        );

        // 从Authentication中拿到用户信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setToken(jwtUtils.generateToken(username));
        responseDTO.setRefreshToken(jwtUtils.generateRefreshToken(username));

        //1、userInfo
        UserInfoDTO userInfo = new UserInfoDTO();

        SysUser sysUser = sysUserMapper.findByUsername(username)
                .orElseThrow(() -> new Exception("用户不存在"));

        userInfo.setUsername(sysUser.getUsername());
        userInfo.setNickname(sysUser.getNickname());
        userInfo.setAvatar(sysUser.getAvatar());

        List<String> roleList = sysRoleMapper.selectRoleCodesByUserId(sysUser.getId());
        userInfo.setRoleList(new ArrayList<>(roleList));

        responseDTO.setUserInfo(userInfo);

        //2、moduleList
        List<ModuleDTO> moduleList = sysModuleMapper.selectModulesByUserId(sysUser.getId())
                .stream()
                .map(m -> BeanUtil.copyProperties(m, ModuleDTO.class))
                .toList();
        responseDTO.setModuleList(moduleList);

        //3、menuList
        List<SysMenu> sysMenus = sysMenuMapper.selectMenusByUserId(sysUser.getId());

        List<MenuDTO> menuDTOList = sysMenus.stream()
                .map(m -> BeanUtil.copyProperties(m, MenuDTO.class))
                .toList();

        for (MenuDTO dto: menuDTOList) {
            setChildren(dto, menuDTOList);
        }

        List<MenuDTO> rootList = menuDTOList.stream()
                .filter(m -> m.getParentId() == null || m.getParentId() == 0)
                .toList();

        responseDTO.setMenuList(rootList);

        //4、perms


        return responseDTO;
    }

    private void setChildren(MenuDTO menuDTO, List<MenuDTO> menuDTOList) {
        List<MenuDTO> children = menuDTOList.stream()
                .filter(m -> m.getParentId().equals(menuDTO.getId()))
                .toList();

        menuDTO.setChildren(children);

        for(MenuDTO child : children) {
            setChildren(child, menuDTOList);
        }
    }

    @Override
    public RefreshTokenResponseDTO refreshToken(String refreshToken) throws Exception {
        // 验证 refresh token 是否有效
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new Exception("Refresh token 不能为空");
        }

        // 检查 refresh token 是否过期
        if (jwtUtils.isTokenExpired(refreshToken)) {
            throw new Exception("Refresh token 已过期，请重新登录");
        }

        // 从 refresh token 中获取用户名
        String username = jwtUtils.getUsernameFromToken(refreshToken);
        
        // 验证用户是否存在
        SysUser sysUser = sysUserMapper.findByUsername(username)
                .orElseThrow(() -> new Exception("用户不存在"));

        // 生成新的 token 和 refresh token
        RefreshTokenResponseDTO responseDTO = new RefreshTokenResponseDTO();
        responseDTO.setToken(jwtUtils.generateToken(username));
        responseDTO.setRefreshToken(jwtUtils.generateRefreshToken(username));
        
        return responseDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterRequestDTO requestDTO) {
        SysUser user = new SysUser();
        user.setUsername(requestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setEmail(requestDTO.getEmail());
        user.setPhone(requestDTO.getPhone());
        user.setNickname(requestDTO.getNickname());
        user.setCreateTime(LocalDateTime.now());
        sysUserMapper.insert(user);
    }

    @Override
    public Optional<SysUser> findByUsername(String username) {
        return sysUserMapper.findByUsername(username);
    }

}
