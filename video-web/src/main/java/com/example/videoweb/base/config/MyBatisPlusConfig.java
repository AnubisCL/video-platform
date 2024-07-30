package com.example.videoweb.base.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.example.videoweb.base.handler.MysqlMetaObjectHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: anubis
 * @Date: 2024/7/23 14:51
 */
@Configuration
@MapperScan(basePackages = {"com.example.videoweb.mapper"})
public class MyBatisPlusConfig {

    /**
     * 添加自动填充插件
     */
    @Bean
    MysqlMetaObjectHandler mysqlMetaObjectHandler() {
        return new MysqlMetaObjectHandler();
    }

    /**
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 如果配置多个插件, 切记分页最后添加
        // 如果有多数据源可以不配具体类型, 否则都建议配上具体的 DbType
        return interceptor;
    }


}
