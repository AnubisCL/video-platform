package com.example.videoweb;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

@SpringBootApplication
public class VideoWebApplication {

    @SneakyThrows
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(VideoWebApplication.class, args);
        Environment env = context.getEnvironment();
        // 获取Spring Boot应用的本地IP和端口号
        InetAddress inetAddress = InetAddress.getLocalHost();
        String localIp = inetAddress.getHostAddress();
        int localPort = Integer.parseInt(env.getProperty("server.port"));
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
