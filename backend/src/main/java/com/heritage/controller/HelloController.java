package com.heritage.controller;

import com.heritage.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 自检接口（开发阶段使用）
 *
 * <p>用于验证后端服务与数据库连通性；业务模块开发完成后可整体删除。</p>
 */
@RestController
@RequestMapping("/api/hello")
@RequiredArgsConstructor
public class HelloController {

    private final JdbcTemplate jdbcTemplate;

    /** 服务连通性检查 */
    @GetMapping
    public Result<String> hello() {
        return Result.ok("heritage-teaching-platform 后端服务运行中");
    }

    /** 数据库连通性检查 */
    @GetMapping("/db")
    public Result<String> db() {
        String now = jdbcTemplate.queryForObject("SELECT NOW()", String.class);
        return Result.ok("数据库连接正常，数据库服务器时间：" + now);
    }
}
