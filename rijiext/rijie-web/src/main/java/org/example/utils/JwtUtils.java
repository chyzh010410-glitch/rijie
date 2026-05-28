package org.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtils {

    private static Key SECRET_KEY;
    private static final long EXPIRATION_TIME = 3 * 60 * 60 * 1000;

    /**
     * 从配置注入密钥，避免每次重启生成新密钥导致所有token失效。
     * 未配置时回退到随机密钥（开发环境可接受）。
     */
    public static void initSecretKey(String base64Secret) {
        if (base64Secret != null && !base64Secret.isBlank()) {
            try {
                byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
                SECRET_KEY = Keys.hmacShaKeyFor(keyBytes);
                log.info("JWT密钥已从配置加载");
            } catch (Exception e) {
                log.warn("JWT配置解码失败，使用随机密钥: {}", e.getMessage());
                SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            }
        } else {
            SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            log.warn("未配置jwt.secret，使用随机密钥（重启后所有token将失效）");
        }
    }

    private static Key getKey() {
        if (SECRET_KEY == null) {
            SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
        return SECRET_KEY;
    }

    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getKey())
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
