package com.heritage.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置
 *
 * <p>分页插件在本类注册；逻辑删除通过 application.yml 的
 * mybatis-plus.global-config.db-config 全局配置（logic-delete-field=deleted），
 * 实体类中包含 deleted 字段即自动生效，无需逐个标注 @TableLogic。</p>
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * MyBatis-Plus 插件链
     *
     * <p>注册分页插件后，Mapper 直接传入 com.baomidou.mybatisplus.extension.plugins.pagination.Page
     * 即可实现 MySQL 物理分页（自动拼 LIMIT 并自动执行 COUNT）。</p>
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 单页最大条数限制，防止恶意大分页拖垮数据库
        paginationInterceptor.setMaxLimit(100L);
        interceptor.addInnerInterceptor(paginationInterceptor);
        return interceptor;
    }
}
