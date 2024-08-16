package com.example.videoweb.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: anubis
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
