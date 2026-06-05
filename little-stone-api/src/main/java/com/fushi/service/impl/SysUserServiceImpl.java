package com.fushi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.fushi.dto.auth.*;
import com.fushi.entity.SysMenu;
import com.fushi.entity.SysUser;
import com.fushi.mapper.SysMenuMapper;
import com.fushi.mapper.SysModuleMapper;
import com.fushi.mapper.SysRoleMapper;
import com.fushi.mapper.SysUserMapper;
import com.fushi.security.util.JwtTokenUtil;
import com.fushi.service.SysUserService;
import com.fushi.service.PasswordService;
import com.fushi.util.EncryptionUtil;
import com.fushi.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysModuleMapper sysModuleMapper;
    private final SysMenuMapper sysMenuMapper;
    private final PasswordService passwordService;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    private String buildRefreshTokenKey(String username, String deviceFingerprint) {
        return REFRESH_TOKEN_PREFIX + username + ":" + deviceFingerprint;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO requestDTO) throws Exception {
        // 通过 nonce 从 Redis 中获取对应的 salt
        String salt = passwordService.getSaltByNonce(requestDTO.getNonce());
        
        // 使用 salt 解密密码
        String password = EncryptionUtil.decrypt(requestDTO.getEncryptedPassword(), salt);
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.getUsername(), password)
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setToken(jwtUtils.generateToken(username));

        String refreshToken = jwtUtils.generateRefreshToken(username);
        String refreshTokenKey = buildRefreshTokenKey(username, requestDTO.getDeviceFingerprint());
        redisUtil.set(refreshTokenKey, refreshToken, jwtUtils.refreshExpiration, TimeUnit.MILLISECONDS);

        SysUser sysUser = sysUserMapper.findByUsername(username)
                .orElseThrow(() -> new Exception("用户不存在"));

        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setUsername(sysUser.getUsername());
        userInfo.setNickname(sysUser.getNickname());
        userInfo.setAvatar(sysUser.getAvatar());

        List<String> roleList = sysRoleMapper.selectRoleCodesByUserId(sysUser.getId());
        userInfo.setRoleList(new ArrayList<>(roleList));
        responseDTO.setUserInfo(userInfo);

        List<ModuleDTO> moduleList = sysModuleMapper.selectModulesByUserId(sysUser.getId())
                .stream()
                .map(m -> BeanUtil.copyProperties(m, ModuleDTO.class))
                .toList();
        responseDTO.setModuleList(moduleList);

        List<SysMenu> sysMenus = sysMenuMapper.selectMenusByUserId(sysUser.getId());
        List<MenuDTO> menuDTOList = sysMenus.stream()
                .map(m -> BeanUtil.copyProperties(m, MenuDTO.class))
                .toList();

        for (MenuDTO dto : menuDTOList) {
            setChildren(dto, menuDTOList);
        }

        List<MenuDTO> rootList = menuDTOList.stream()
                .filter(m -> m.getParentId() == null || m.getParentId() == 0)
                .toList();
        responseDTO.setMenuList(rootList);

        return responseDTO;
    }

    private void setChildren(MenuDTO menuDTO, List<MenuDTO> menuDTOList) {
        List<MenuDTO> children = menuDTOList.stream()
                .filter(m -> m.getParentId().equals(menuDTO.getId()))
                .toList();

        menuDTO.setChildren(children);

        for (MenuDTO child : children) {
            setChildren(child, menuDTOList);
        }
    }

    @Override
    public RefreshTokenResponseDTO refreshToken(String deviceFingerprint) throws Exception {
        if (deviceFingerprint == null || deviceFingerprint.trim().isEmpty()) {
            throw new Exception("设备指纹不能为空");
        }

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        String username = userDetails.getUsername();

        String refreshTokenKey = buildRefreshTokenKey(username, deviceFingerprint);
        String refreshToken = redisUtil.get(refreshTokenKey, String.class);

        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new Exception("Refresh token 不存在或已过期，请重新登录");
        }

        if (jwtUtils.isTokenExpired(refreshToken)) {
            redisUtil.delete(refreshTokenKey);
            throw new Exception("Refresh token 已过期，请重新登录");
        }

        RefreshTokenResponseDTO responseDTO = new RefreshTokenResponseDTO();
        responseDTO.setToken(jwtUtils.generateToken(username));

        String newRefreshToken = jwtUtils.generateRefreshToken(username);
        redisUtil.set(refreshTokenKey, newRefreshToken, jwtUtils.refreshExpiration, TimeUnit.MILLISECONDS);

        return responseDTO;
    }

    @Override
    public void logout(String deviceFingerprint) throws Exception {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        String username = userDetails.getUsername();

        String refreshTokenKey = buildRefreshTokenKey(username, deviceFingerprint);
        redisUtil.delete(refreshTokenKey);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterRequestDTO requestDTO) throws Exception {
        // 通过 nonce 从 Redis 中获取对应的 salt
        String salt = passwordService.getSaltByNonce(requestDTO.getNonce());
        
        // 使用 salt 解密密码
        String password = EncryptionUtil.decrypt(requestDTO.getEncryptedPassword(), salt);
        
        SysUser user = new SysUser();
        user.setUsername(requestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(password));
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
