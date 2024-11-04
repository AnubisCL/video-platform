package com.example.videoweb.base.aop;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.example.videoweb.base.annotation.FrequencyControl;
import com.example.videoweb.utils.EhcacheUtil;
import com.example.videoweb.utils.SpElUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 频率控制切面
 * @Author: chailei
 * @Date: 2023/6/16 10:14
 */
@Slf4j
@Order(4)
@Aspect
@Component
public class FrequencyControlAspect {


    @Around("@annotation(com.example.videoweb.base.annotation.FrequencyControl)||@annotation(com.example.videoweb.base.annotation.FrequencyControlContainer)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前方法的Method对象
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // 获取方法上的FrequencyControl注解数组
        FrequencyControl[] annotationsByType = method.getAnnotationsByType(FrequencyControl.class);
        // 定义一个Map对象，用于存储key-FrequencyControl的键值对
        Map<String, FrequencyControl> keyMap = new HashMap<>();
        for (int i = 0; i < annotationsByType.length; i++) {
            // 获取当前注解
            FrequencyControl frequencyControl = annotationsByType[i];
            // 获取方法上的注解排名（可能多个）
            String prefix = StrUtil.isBlank(frequencyControl.prefixKey()) ? SpElUtils.getMethodKey(method) + ":index:" + i : frequencyControl.prefixKey(); //默认方法限定名+注解排名（可能多个）
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String key = "";
                switch (frequencyControl.target()) {
                    case EL:
                        key = SpElUtils.parseSpEl(method, joinPoint.getArgs(), frequencyControl.spEl());
                        break;
                    case IP:
                        key = request.getRemoteHost();
                        break;
                    case USERID:
                        key = StpUtil.getLoginIdAsString();
                }
                // 将key-FrequencyControl的键值对添加到keyMap中
                keyMap.put(prefix + ":" + key, frequencyControl);
            }
        }

        ArrayList<String> keyList = new ArrayList<>(keyMap.keySet());
        for (int i = 0; i < keyList.size(); i++) {
            String key = keyList.get(i);
            Integer count = (Integer) EhcacheUtil.get(key);
            FrequencyControl frequencyControl = keyMap.get(key);
            // 如果统计值不为空且频率超过了限制
            if (Objects.nonNull(count) && count >= frequencyControl.count()) {//频率超过了
                log.warn("frequencyControl limit key:{},count:{}", key, count);
                // 抛出业务异常，表示频率限制
                throw new Exception("频率限制");
            }
        }
        try {
            // 调用原始方法
            return joinPoint.proceed();
        } finally {
            // 不管成功还是失败，都增加次数
            keyMap.forEach((k, v) -> {
                int currentCount = EhcacheUtil.get(k) != null ? (Integer) EhcacheUtil.get(k) : 0;
                // 设置过期时间
                EhcacheUtil.set(k, currentCount + 1, v.unit().toMillis(v.time()));
            });
        }
        /*
        //批量获取redis统计的count值
        ArrayList<String> keyList = new ArrayList<>(keyMap.keySet());
        List<Integer> countList = RedisUtils.mget(keyList, Integer.class);
        for (int i = 0; i < keyList.size(); i++) {
            String key = keyList.get(i);
            Integer count = countList.get(i);
            FrequencyControl frequencyControl = keyMap.get(key);
            // 如果统计值不为空且频率超过了限制
            if (Objects.nonNull(count) && count >= frequencyControl.count()) {//频率超过了
                log.warn("frequencyControl limit key:{},count:{}", key, count);
                // 抛出业务异常，表示频率限制
                throw new Exception("频率限制");
            }
        }
        try {
            // 调用原始方法
            return joinPoint.proceed();
        } finally {
            //不管成功还是失败，都增加次数
            keyMap.forEach((k, v) -> {
                RedisUtils.inc(k, v.time(), v.unit());
            });
        }*/
    }
}
