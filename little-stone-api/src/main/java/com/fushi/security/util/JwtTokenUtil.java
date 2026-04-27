package com.fushi.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.lang.IllegalArgumentException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    @Value("${application.security.jwt.secret-key}")
    public String secretKey;

    @Value("${application.security.jwt.expiration}")
    public Long expiration;

    private SecretKey signingKey;
    private JwtParser jwtParser;

    private Key getSigningKey() {
        if(signingKey == null) {
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            if(keyBytes.length < 32) {
                throw new IllegalArgumentException("JWT密钥长度不足，至少需要32个字节(256位)");
            }
            return Keys.hmacShaKeyFor(keyBytes);
        }
        return signingKey;
    }

    //初始化 JWT解析器
    private JwtParser getJwtParser() {
        if(jwtParser == null) {
            jwtParser = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build();
        }
        return jwtParser;
    }

    //从令牌获取用户名
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //从令牌获取过期时间
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getCustomClaimFromToken(String token, String claimName, Class<T> claimType) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get(claimName, claimType);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        if(!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("JWT令牌不能为空！");
        }

        try {
            return getJwtParser()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw new ExpiredJwtException(ex.getHeader(), ex.getClaims(), "JWT令牌已过期！", ex);
        } catch (IllegalArgumentException ex) {
            throw new SignatureException("JWT令牌解析失败：" + ex.getMessage(), ex);
        }
    }

    //检验令牌
    public Boolean validateToken(String token, UserDetails userDetails) {
        if(!StringUtils.hasText(token) || userDetails == null) {
            throw new IllegalArgumentException("令牌或用户信息不能为空！");
        }

        final String username = getUsernameFromToken(token);
        if(!username.equals(userDetails.getUsername())) {
            throw new SignatureException("令牌中的用户名与当前用户不匹配");
        }

        if(isTokenExpired(token)) {
            throw new ExpiredJwtException(null, null, "JWT令牌已过期！");
        }

        return true;
    }

    //检查令牌是否过期
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //生成 token
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, username);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
