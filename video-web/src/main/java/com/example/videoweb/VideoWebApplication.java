package com.example.videoweb;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;
import java.util.Objects;

@EnableTransactionManagement
@EnableScheduling
@SpringBootApplication
public class VideoWebApplication {

    @SneakyThrows
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(VideoWebApplication.class, args);
        Environment env = context.getEnvironment();
        // 获取Spring Boot应用的本地IP和端口号
        InetAddress inetAddress = InetAddress.getLocalHost();
        String localIp = inetAddress.getHostAddress();
        int localPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        // 从配置文件中读取swagger-ui路径
        String swaggerPath = env.getProperty("springdoc.swagger-ui.path");
        // 打印swagger-ui的访问URL
        System.out.printf("""
                ----------------------------------------------------------------------
                |    vide-web  当前环境 %s 启动成功
                |    springdoc http://%s:%d%s
                ----------------------------------------------------------------------
                """, env.getActiveProfiles()[0], localIp, localPort, swaggerPath);
    }



}
