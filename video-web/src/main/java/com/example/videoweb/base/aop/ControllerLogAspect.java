package com.example.videoweb.base.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @Author: chailei
 * @Date: 2024/8/2 13:55
 */
@Slf4j
@Aspect
@Component
public class ControllerLogAspect {

    private static HashSet<String> filterPrintSet;
    static {
        filterPrintSet = new HashSet<>();
        filterPrintSet.add("/get-avatar");
        filterPrintSet.add("/isLogin");
    }

    @Pointcut("execution(* com.example.videoweb.controller..*(..))")
    public void requestServer() {
    }

    @Around("requestServer()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURL().toString();
        String[] splitUrl = url.split("/");
        boolean isPrint = !filterPrintSet.contains("/"+splitUrl[splitUrl.length - 1]);
        Object result = null;
        if (isPrint) {
            log.info("=======================Request Start========================");
            log.info("Remote IP      : {}", request.getRemoteAddr());
            log.info("URL            : {}", url);
            log.info("HTTP Method    : {}", request.getMethod());
            log.info("Request Params : {}", getRequestParams(proceedingJoinPoint));
            log.info("=======================Request End==========================");
            result = proceedingJoinPoint.proceed();
            log.info("=======================Response Start=======================");
            log.info("Result         : {}", result);
            log.info("Time Cost      : {} ms", System.currentTimeMillis() - start);
            log.info("=======================Response End=========================");
        } else {
            result = proceedingJoinPoint.proceed();
        }
        return result;
    }

    private Map<String, Object> getRequestParams(ProceedingJoinPoint proceedingJoinPoint) {
        Map<String, Object> requestParams = new HashMap<>();
        //参数名
        String[] paramNames = ((MethodSignature) proceedingJoinPoint.getSignature()).getParameterNames();
        //参数值
        Object[] paramValues = proceedingJoinPoint.getArgs();
        for (int i = 0; i < paramNames.length; i++) {
            Object value = paramValues[i];
            //如果是文件对象
            if (value instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) value;
                value = file.getOriginalFilename();  //获取文件名
            }
            requestParams.put(paramNames[i], value);
        }

        return requestParams;
    }
}
