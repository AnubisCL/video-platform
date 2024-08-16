package com.example.videoweb.base.config;

import com.example.videoweb.base.factory.MyThreadFactory;
import com.example.videoweb.base.properties.ThreadPoolConfigProperties;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description: 公共线程池
 * @Author: anubis
 * @Date: 2024/7/23 14:51
 */
@Configuration
public class ThreadPoolTaskExecutorConfig {

    @Resource private ThreadPoolConfigProperties config;

    @Bean
    public ThreadPoolTaskExecutor videoExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        //CPU 密集型任务：CPU 核心数加 1
        //IO 密集型任务：CPU 核心数的 2 倍或多一点
        executor.setCorePoolSize(config.getCorePoolSize());
        // 设置最大线程数
        executor.setMaxPoolSize(config.getMaxPoolSize());
        // 设置队列容量
        executor.setQueueCapacity(config.getQueueCapacity());
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(config.getKeepAliveSeconds());
        // 设置默认线程名称
        executor.setThreadNamePrefix(config.getThreadNamePrefix());
        // 设置拒绝策略
        if ("CallerRunsPolicy".equals(config.getRejectedExecutionHandler())) {
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            // CallerRunsPolicy 策略下，当线程池和队列都满时，调用者所在的线程会执行该任务。
            // 这意味着调用 execute 方法的线程将执行任务，而不是将其丢弃或阻止线程池。
        }
        else if ("AbortPolicy".equals(config.getRejectedExecutionHandler())) {
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
            // AbortPolicy 策略下，当线程池和队列都满时，抛出 RejectedExecutionException 异常。
            // 这是一种失败快的策略，可以立即反馈给调用者线程池已满的信息。
        }
        else if ("DiscardPolicy".equals(config.getRejectedExecutionHandler())) {
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
            // DiscardPolicy 策略下，当线程池和队列都满时，会默默地丢弃任务而不执行。
            // 这种策略不会抛出异常，也不会阻塞调用者，但它会导致任务丢失。
        }
        else if ("DiscardOldestPolicy".equals(config.getRejectedExecutionHandler())) {
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
            // DiscardOldestPolicy 策略下，当线程池和队列都满时，会从队列中移除最旧的任务，然后尝试重新提交当前任务。
            // 这种策略试图通过牺牲最旧的任务来为新任务腾出空间，从而避免任务的完全丢失。
        }
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置全局未捕获异常打印日志
        executor.setThreadFactory(new MyThreadFactory(executor));
        executor.initialize();
        return executor;
    }
}
