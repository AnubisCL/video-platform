package com.example.videoweb.base.factory;

import com.example.videoweb.base.handler.GlobalUncaughtExceptionHandler;
import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadFactory;

/**
 * @Author: anubis
 * @Date: 2024/7/23 18:04
 */
@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {

    private final ThreadFactory factory;

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = factory.newThread(runnable);
        thread.setUncaughtExceptionHandler(GlobalUncaughtExceptionHandler.getInstance());
        return thread;
    }
}
