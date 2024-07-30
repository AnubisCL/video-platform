package com.example.videoweb.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: chailei
 * @Date: 2024/7/30 14:33
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "thread-pool")
public class ThreadPoolConfigProperties {

    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private int keepAliveSeconds;
    private String threadNamePrefix;
    private String rejectedExecutionHandler;

}
