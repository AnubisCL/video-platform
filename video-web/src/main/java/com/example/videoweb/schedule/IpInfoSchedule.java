package com.example.videoweb.schedule;

import com.alibaba.fastjson.JSON;
import com.example.videoweb.base.config.CacheConfig;
import com.example.videoweb.base.utils.IpUtil;
import com.example.videoweb.base.utils.ProcessUtil;
import com.example.videoweb.domain.cache.IpInfo;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chailei
 * @Date: 2024/8/10 12:16
 */
@Slf4j
@Component
public class IpInfoSchedule {

    private static final String GET_IPV6 = "6.ipw.cn";
    private static final String TEST_IP_TYPE = "test.ipw.cn";

    @Value("${nginx-config.protocol-type.ipv4.lan-name}")
    private String ipv4LanName;
    @Resource @Qualifier("ehCacheManager") private CacheManager ehCacheManager;

    @Scheduled(cron = "0 0 * * * ? ")
    public void updateIpv4AndIpv6Schedule() {
        try {
            Cache<String, IpInfo> ipCache = ehCacheManager.getCache(CacheConfig.IP_CACHE_NAME, String.class, IpInfo.class);
            IpInfo oldIpInfo = ipCache.get(CacheConfig.IP_CACHE_NAME);
            log.info("获取磁盘缓存 ipInfo: {}", JSON.toJSONString(oldIpInfo));

            IpInfo newIpInfo = new IpInfo();

            String testIPResult = ProcessUtil.executeCommandWithResult(Arrays.asList("curl", TEST_IP_TYPE), 3, TimeUnit.SECONDS);
            InetAddress inetAddress = InetAddress.getByName(testIPResult);
            if (inetAddress instanceof java.net.Inet4Address) {
                log.info("当前 IPV4 优先: {}" , testIPResult);
            } else if (inetAddress instanceof java.net.Inet6Address) {
                log.info("当前 IPV6 优先: {}" , testIPResult);
            }

            // 获取IPV4地址
            String ipv4Address = IpUtil.getWifiIpAddress(ipv4LanName);
            log.info("获取 ipv4Address 地址: {}", ipv4Address);
            if (!StringUtils.isEmpty(ipv4Address)) {
                boolean isLocalAreaNetwork = ipv4Address.startsWith("192.168");
                if (isLocalAreaNetwork) {
                    newIpInfo.setIpv4(ipv4Address);
                }
                newIpInfo.setIsIpv4(isLocalAreaNetwork);
            } else {
                newIpInfo.setIsIpv4(false);
                log.warn("获取 ipv4Address 为 NULL.");
            }

            // 获取IPV6地址
            String ipv6Address = ProcessUtil.executeCommandWithResult(Arrays.asList("curl", GET_IPV6), 3, TimeUnit.SECONDS);
            log.info("获取 ipv6Address 地址: {}", ipv6Address);
            if (!StringUtils.isEmpty(ipv6Address) && testIPResult.equals(ipv6Address)) {
                newIpInfo.setIsIpv6(true);
                newIpInfo.setIpv6(ipv6Address);
            } else {
                newIpInfo.setIsIpv6(false);
                log.warn("获取 ipv6Address 为 NULL.");
            }

            ipCache.put(CacheConfig.IP_CACHE_NAME, newIpInfo);
            log.info("刷新磁盘缓存 ipInfo: {}", JSON.toJSONString(newIpInfo));
        } catch (Exception e) {
            log.error("获取 IP Address error: {}", e.getMessage());
        }
    }

}
