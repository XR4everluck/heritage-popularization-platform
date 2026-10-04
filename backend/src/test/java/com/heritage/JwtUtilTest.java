package com.heritage;

import com.heritage.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * JWT 工具单元测试（纯算法测试，不依赖数据库/容器）：
 * 覆盖生成、解析、有效校验、篡改失效、过期失效。
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "unit-test-secret-0123456789abcdef0123456789abcdef");
        ReflectionTestUtils.setField(jwtUtil, "expireHours", 1L);
        jwtUtil.initKey();
    }

    @Test
    void generateParseAndValidate() {
        String token = jwtUtil.generateToken(2L, "user1", "user");
        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isEmpty());

        Claims claims = jwtUtil.parse(token);
        Assertions.assertEquals("2", claims.getSubject());
        Assertions.assertEquals("user1", claims.get("username"));
        Assertions.assertEquals("user", claims.get("role"));
        Assertions.assertTrue(jwtUtil.validate(token), "未过期的合法 token 应校验通过");
    }

    @Test
    void invalidTokensMustFail() {
        Assertions.assertFalse(jwtUtil.validate("not-a-token"), "非法字符串不应通过校验");
        String token = jwtUtil.generateToken(1L, "admin", "admin");
        Assertions.assertFalse(jwtUtil.validate(token + "x"), "被篡改的 token 不应通过校验");
        // 用不同密钥签出的 token 也不应通过
        JwtUtil other = new JwtUtil();
        ReflectionTestUtils.setField(other, "secret", "another-secret-0123456789abcdef0123456789abcd");
        ReflectionTestUtils.setField(other, "expireHours", 1L);
        other.initKey();
        Assertions.assertFalse(jwtUtil.validate(other.generateToken(1L, "admin", "admin")), "密钥不一致的 token 不应通过校验");
    }

    @Test
    void expiredTokenMustFail() {
        ReflectionTestUtils.setField(jwtUtil, "expireHours", -1L);
        String token = jwtUtil.generateToken(1L, "admin", "admin");
        Assertions.assertFalse(jwtUtil.validate(token), "过期 token 不应通过校验");
    }
}
