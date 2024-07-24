package com.example.videoweb.base.config;

import com.example.videoweb.base.handler.MysqlMetaObjectHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: chailei
 * @Date: 2024/7/23 14:51
 */
@Configuration
@MapperScan(basePackages = {"com.example.videoweb.mapper"})
public class MyBatisPlusConfig {

    @Bean
    MysqlMetaObjectHandler mysqlMetaObjectHandler() {
        return new MysqlMetaObjectHandler();
    }

}