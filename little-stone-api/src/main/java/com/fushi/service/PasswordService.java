package com.fushi.service;

import com.fushi.dto.auth.SaltResponseDTO;

public interface PasswordService {
    
    /**
     * 获取用户密码盐
     * @param username 用户名
     * @return 盐值响应对象
     */
    SaltResponseDTO getSalt(String username) throws Exception;
    
    /**
     * 通过 nonce 获取 salt
     * @param nonce 随机数
     * @return 盐值
     */
    String getSaltByNonce(String nonce) throws Exception;
}
