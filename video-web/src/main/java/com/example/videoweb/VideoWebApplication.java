package com.example.videoweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan(basePackages = "com.example.videoweb.mapper")
public class VideoWebApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(VideoWebApplication.class, args);
        String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        System.out.printf("\n--- vide-web 当前环境 %s 启动成功 ---\n\n", activeProfiles[0]);
    }

}
