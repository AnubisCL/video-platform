package com.example.videoweb.base.handler;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: chailei
 * @Date: 2024/7/23 18:05
 */

@Slf4j
public class GlobalUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final GlobalUncaughtExceptionHandler instance = new GlobalUncaughtExceptionHandler();

    private GlobalUncaughtExceptionHandler() {
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception in thread {} ", t.getName(), e);
    }

    public static GlobalUncaughtExceptionHandler getInstance() {
        return instance;
    }

}
