package com.fushi.controller;

import com.fushi.common.enums.ResultCode;
import com.fushi.common.response.ApiResponse;
import com.fushi.dto.LoginRequestDTO;
import com.fushi.dto.LoginResponseDTO;
import com.fushi.dto.UserAddDTO;
import com.fushi.entity.User;
import com.fushi.mapper.UserMapper;
import com.fushi.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO authRequest) {
        String username = authRequest.getUsername();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            log.warn("用户[{}]登录失败：密码错误", username);
            return ApiResponse.error(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
        } catch (Exception e) {
            log.warn("用户[{}]登录异常！", username, e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), "登录失败，请联系管理员！");
        }

        try {
            LoginResponseDTO responseDTO = userService.getLoginUserInfo(username);
            log.info("用户[{}]登录成功", username);
            return ApiResponse.success(responseDTO);
        } catch (Exception e) {
            log.error("用户[{}]生成令牌异常！", username, e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), "生成令牌失败");
        }

    }

    @PostMapping("/regist")
    public ApiResponse<?> regist(@RequestBody UserAddDTO dto) {
        try {
            //判断用户名是否已经存在
            Optional<User> user = userMapper.findByUsername(dto.getUsername());
            if (user.isPresent()) {
                return ApiResponse.error(ResultCode.USER_EXIST.getCode(), "用户名已存在");
            }

            User newUser = new User();
            newUser.setUsername(dto.getUsername());
            newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
            newUser.setEmail(dto.getEmail());
            newUser.setPhone(dto.getPhone());
            newUser.setRealName(dto.getRealName());
            newUser.setCreateTime(LocalDateTime.now());
            userMapper.insert(newUser);
            log.info("用户[{}]注册成功", dto.getUsername());
            return ApiResponse.success("用户注册成功");
        } catch (Exception e) {
            log.error("用户[{}]注册异常！", dto.getUsername(), e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), "用户注册失败");
        }
    }
}
