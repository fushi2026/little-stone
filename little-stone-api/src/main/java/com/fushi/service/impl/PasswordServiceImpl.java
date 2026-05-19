package com.fushi.service.impl;

import com.fushi.dto.auth.SaltResponseDTO;
import com.fushi.service.PasswordService;
import com.fushi.util.EncryptionUtil;
import com.fushi.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {
    
    private final RedisUtil redisUtil;
    
    private static final String SALT_PREFIX = "salt:";
    private static final long SALT_EXPIRATION = 5; // 盐值过期时间（分钟）

    @Override
    public SaltResponseDTO getSalt(String username) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("用户名不能为空");
        }

        // 生成 nonce 和 salt
        String nonce = EncryptionUtil.generateNonce();
        String salt = EncryptionUtil.generateSalt();

        // 将 nonce 作为 key，salt 作为 value 存储到 Redis 中，设置过期时间
        String redisKey = SALT_PREFIX + nonce;
        redisUtil.set(redisKey, salt, SALT_EXPIRATION, TimeUnit.MINUTES);

        log.info("为用户[{}]生成盐值和nonce成功", username);

        return new SaltResponseDTO(nonce, salt);
    }

    @Override
    public String getSaltByNonce(String nonce) throws Exception {
        if (nonce == null || nonce.trim().isEmpty()) {
            throw new Exception("nonce不能为空");
        }

        // 通过 nonce 从 Redis 中获取对应的 salt
        String redisKey = SALT_PREFIX + nonce;
        String salt = redisUtil.get(redisKey, String.class);

        if (salt == null || salt.trim().isEmpty()) {
            throw new Exception("nonce已过期或无效，请重新获取盐值");
        }

        log.debug("通过nonce[{}]获取salt成功", nonce);

        return salt;
    }
}
