package com.example.videoweb.base.aop;

import com.example.videoweb.base.annotation.ReplaceIp;
import com.example.videoweb.base.annotation.ReplaceIpEntity;
import com.example.videoweb.base.config.CacheConfig;
import com.example.videoweb.domain.cache.IpInfo;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.utils.IpUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @Author: chailei
 * @Date: 2024/10/21 15:20
 */
@Order(3)
@Aspect
@Component
public class IpReplaceAspect {

    @Resource
    @Qualifier("ehCacheManager") private CacheManager cacheManager;

    @Value("${nginx-config.protocol-type.local.name}") private String local;
    @Value("${nginx-config.protocol-type.local.local-ipv4-ip}") private String localIp;
    @Value("${nginx-config.protocol-type.ipv4.name}") private String ipv4;
    @Value("${nginx-config.protocol-type.ipv6.name}") private String ipv6;
    @Value("${nginx-config.protocol-type.domain.name}") private String domain;
    @Value("${nginx-config.protocol-type.domain.host}") private String domainHost;

    @Around("@annotation(com.example.videoweb.base.annotation.ReplaceIpFun)")
    public Object aroundReplaceIp(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result instanceof ResultVo) {
            ResultVo resultVo = (ResultVo) result;

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String protocolType = IpUtil.getIpAddressProtocolType(request, domainHost);
                IpInfo ipInfo = cacheManager.getCache(CacheConfig.IP_CACHE_NAME, String.class, IpInfo.class).get(CacheConfig.IP_CACHE_NAME);

                Object data = resultVo.getData();
                if (data instanceof String) { // 直接返回带IP的String
                    String value = (String) data;
                    String replacedValue = replaceIp(value, protocolType, ipInfo);
                    ((ResultVo) result).setData(replacedValue);
                } else if(data instanceof List) {
                    List dataList =  (List) data;
                    for (Object object : dataList) {
                        if (object.getClass().isAnnotationPresent(ReplaceIpEntity.class)) {
                            getReplaceIpAnnotation(object, protocolType, ipInfo);
                        }
                    }
                } else if (data.getClass().isAnnotationPresent(ReplaceIpEntity.class)) {
                    getReplaceIpAnnotation(data, protocolType, ipInfo);
                }
            }
        }

        return result;
    }

    private void getReplaceIpAnnotation(Object object, String protocolType, IpInfo ipInfo) {
        Class<?> clazz = object.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();

        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(ReplaceIpEntity.class)) {
                declaredField.setAccessible(true);
                try {
                    Object nestedObject = declaredField.get(object);
                    if (nestedObject != null) {
                        if (nestedObject instanceof List) {
                            List nestedObjectList = (List)nestedObject;
                            for (Object o : nestedObjectList) {
                                getReplaceIpAnnotation(o, protocolType, ipInfo);
                            }
                        } else {
                            getReplaceIpAnnotation(nestedObject, protocolType, ipInfo);
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            if (declaredField.isAnnotationPresent(ReplaceIp.class)) {
                declaredField.setAccessible(true);
                try {
                    Object fieldValue = declaredField.get(object);
                    if (fieldValue instanceof String) {
                        String replacedValue = replaceIp((String) fieldValue, protocolType, ipInfo);
                        declaredField.set(object, replacedValue);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    private String replaceIp(String originalValue, String protocolType, IpInfo ipInfo) {
        if (protocolType.equals(domain)) {
            return originalValue.replace(localIp, domainHost);
        }
        if (protocolType.equals(ipv6) && ipInfo.getIsIpv6()) {
            String replaceIpv6 = "[" + ipInfo.getIpv6() + "]";
            return originalValue.replace(localIp, replaceIpv6);
        }
        if ((protocolType.equals(ipv4) || protocolType.equals(local)) && ipInfo.getIsIpv4()) {
            return originalValue.replace(localIp, ipInfo.getIpv4());
        }
        if (protocolType.equals(local)) {
            return originalValue;
        }
        return originalValue;
    }
}

