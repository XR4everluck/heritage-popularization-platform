package com.heritage;

import cn.hutool.crypto.digest.BCrypt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 密码加密一致性测试（纯算法测试，不依赖数据库/容器）：
 * 验证建表 SQL 种子账号的 BCrypt 密文与登录校验逻辑使用的算法一致。
 */
class BCryptSeedTest {

    /** 与 sql/heritage_teaching_platform.sql 中种子账号密码一致的密文（明文 123456，$2a$ 前缀） */
    private static final String SEED_HASH = "$2a$10$mzAq8eSHR308mTQFa1CA8OA76LSJ8lvtGTSQzLGYHzQKohCHYMPq.";

    @Test
    void seedPasswordMustVerify() {
        // 登录校验路径：明文密码 vs 种子密文
        Assertions.assertTrue(BCrypt.checkpw("123456", SEED_HASH), "种子密码哈希应能通过校验（admin/user1 登录依赖此断言）");
        Assertions.assertFalse(BCrypt.checkpw("wrong-password", SEED_HASH), "错误密码不应通过校验");

        // 注册路径：hashpw 生成的新密文必须能被 checkpw 校验，且使用 $2a$ 前缀保持一致
        String hash = BCrypt.hashpw("abc123456");
        Assertions.assertTrue(hash.startsWith("$2a$"), "新密文应使用 $2a$ 前缀");
        Assertions.assertTrue(BCrypt.checkpw("abc123456", hash));
    }
}
