package com.example.videoweb;

import cn.dev33.satoken.SaManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@MapperScan(basePackages = "com.example.videoweb.dao")
@SpringBootApplication(scanBasePackages = "com.example.videoweb.*")
public class VideoWebApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(VideoWebApplication.class, args);
        String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        System.out.printf("\n--- vide-web 当前环境 %s 启动成功 ---\n\n", activeProfiles[0]);
        System.out.println("--- Sa-Token 配置如下：" + SaManager.getConfig() + " --- ");
    }

}
