package com.heritage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 非遗知识教学平台后端启动类
 *
 * <p>技术栈：SpringBoot 2.7.18 + MyBatis-Plus 3.5.3.1 + MySQL 8.0</p>
 */
@SpringBootApplication
@MapperScan("com.heritage.mapper")
public class HeritageApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeritageApplication.class, args);
        System.out.println(">>> 非遗知识教学平台后端启动成功，接口地址：http://localhost:8080");
    }
}
