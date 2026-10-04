package com.heritage.util;

import cn.hutool.core.util.RandomUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 工具类：生成 token、解析 token、校验 token
 *
 * <p>签名密钥从环境变量 HERITAGE_JWT_SECRET 读取（至少32字节）；
 * 未配置时自动使用随机密钥并告警——此时重启后所有登录态失效，仅适合开发调试，
 * 生产/答辩演示环境务必配置固定密钥。</p>
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${heritage.jwt.secret:}")
    private String secret;

    /** token 有效期（小时） */
    @Value("${heritage.jwt.expire-hours:24}")
    private Long expireHours;

    private Key signingKey;

    /** 初始化签名密钥（@PostConstruct 自动调用；单测中手动调用） */
    @PostConstruct
    public void initKey() {
        if (secret == null || secret.trim().isEmpty()) {
            // 48位随机串（约248位熵），足够 HS256 使用；仅开发兜底，不做长期密钥
            secret = RandomUtil.randomString(48);
            log.warn("未配置 HERITAGE_JWT_SECRET 环境变量，已使用随机密钥：重启后所有登录态将失效（仅限开发调试）");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 token：subject 存用户ID，附加 username/role 声明
     */
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireHours * 3600_000L))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 token（签名不合法或已过期时抛出 JwtException 子类）
     */
    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 校验 token 是否有效（签名合法且未过期）
     */
    public boolean validate(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
