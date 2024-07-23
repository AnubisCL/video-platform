package com.example.videoweb.config;

import com.example.videoweb.handler.MysqlMetaObjectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: chailei
 * @Date: 2024/7/23 14:51
 */
@Configuration
public class MyBatisPlusConfig {

    @Bean
    MysqlMetaObjectHandler mysqlMetaObjectHandler() {
        return new MysqlMetaObjectHandler();
    }

}
