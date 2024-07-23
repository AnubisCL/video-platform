package com.example.videoweb.base.config;

import com.example.videoweb.base.factory.MyThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description: 公共线程池
 * @Author: chailei
 * @Date: 2024/7/23 14:51
 */
@Configuration
public class ThreadPoolTaskExecutorConfig {

    @Bean
    public ThreadPoolTaskExecutor videoExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(12);
        // 设置最大线程数
        executor.setMaxPoolSize(20);
        // 设置队列容量
        executor.setQueueCapacity(120);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("Video-Web");
        // 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置全局未捕获异常打印日志
        executor.setThreadFactory(new MyThreadFactory(executor));
        executor.initialize();
        return executor;
    }
}
