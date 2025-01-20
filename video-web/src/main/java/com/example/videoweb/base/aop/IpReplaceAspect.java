package com.example.videoweb.base.aop;

import com.example.videoweb.base.annotation.ReplaceIp;
import com.example.videoweb.base.annotation.ReplaceIpEntity;
import com.example.videoweb.base.config.CacheConfig;
import com.example.videoweb.domain.cache.IpInfo;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.utils.IpUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Enumeration;
import java.util.List;


/**
 * @Author: chailei
 * @Date: 2024/10/21 15:20
 */
@Slf4j
@Order(3)
@Aspect
@Component
public class IpReplaceAspect {

    @Resource
    @Qualifier("ehCacheManager") private CacheManager cacheManager;

    @Value("${nginx-config.protocol-type.local.name}") private String local;
    @Value("${nginx-config.protocol-type.local.protocol}") private String protocol;
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
                //origin：https://www.anubis.work.gd:1022
                String scheme = request.getHeader("origin").split("://")[0];
                String protocolType = IpUtil.getIpAddressProtocolType(request, domainHost);
                IpInfo ipInfo = cacheManager.getCache(CacheConfig.IP_CACHE_NAME, String.class, IpInfo.class).get(CacheConfig.IP_CACHE_NAME);

                Object data = resultVo.getData();
                if (data instanceof String) { // 直接返回带IP的String
                    String value = (String) data;
                    String replacedValue = replaceIp(value, protocolType, ipInfo, scheme);
                    ((ResultVo) result).setData(replacedValue);
                } else if(data instanceof List) {
                    List dataList =  (List) data;
                    for (Object object : dataList) {
                        if (object.getClass().isAnnotationPresent(ReplaceIpEntity.class)) {
                            getReplaceIpAnnotation(object, protocolType, ipInfo, scheme);
                        }
                    }
                } else if (data.getClass().isAnnotationPresent(ReplaceIpEntity.class)) {
                    getReplaceIpAnnotation(data, protocolType, ipInfo, scheme);
                }
            }
        }

        return result;
    }

    private void getReplaceIpAnnotation(Object object, String protocolType, IpInfo ipInfo,String scheme) {
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
                                getReplaceIpAnnotation(o, protocolType, ipInfo, scheme);
                            }
                        } else {
                            getReplaceIpAnnotation(nestedObject, protocolType, ipInfo, scheme);
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
                        String replacedValue = replaceIp((String) fieldValue, protocolType, ipInfo, scheme);
                        declaredField.set(object, replacedValue);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    private String replaceIp(String originalValue, String protocolType, IpInfo ipInfo, String scheme) {
        String resultValue = originalValue;
        if (protocolType.equals(domain)) {
            resultValue = originalValue.replace(localIp, domainHost);
        }
        if (protocolType.equals(ipv6) && ipInfo.getIsIpv6()) {
            String replaceIpv6 = "[" + ipInfo.getIpv6() + "]";
            resultValue = originalValue.replace(localIp, replaceIpv6);
        }
        if ((protocolType.equals(ipv4) || protocolType.equals(local)) && ipInfo.getIsIpv4()) {
            resultValue = originalValue.replace(localIp, ipInfo.getIpv4());
        }
        if (protocolType.equals(local)) {
            resultValue = originalValue;
        }
        return replaceProtocol(protocol, scheme, resultValue);
    }

    private String replaceProtocol(String protocol, String scheme, String originalValue) {
        if (protocol.equalsIgnoreCase(scheme)) {
            return originalValue;
        } else {
            return originalValue.replace(protocol, scheme);
        }
    }
}

