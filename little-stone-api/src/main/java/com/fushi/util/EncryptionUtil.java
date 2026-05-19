package com.fushi.util;

import cn.hutool.core.util.RandomUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.UUID;

public class EncryptionUtil {

    public static String generateNonce() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public static String generateSalt() {
        return RandomUtil.randomString(16);
    }

    public static String generateSalt(int length) {
        return RandomUtil.randomString(length);
    }

    public static String decrypt(String cipherText, String saltFromRedis) throws Exception {
        SecretKeySpec key = new SecretKeySpec(saltFromRedis.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decrypted);
    }
}