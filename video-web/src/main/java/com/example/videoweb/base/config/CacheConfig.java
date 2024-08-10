package com.example.videoweb.base.config;

import com.example.videoweb.domain.cache.IpInfo;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;


/**
 * @Author: chailei
 * @Date: 2024/8/10 11:22
 */
@Configuration
public class CacheConfig {

    public static final String IP_CACHE_NAME = "ipCache";

    @Value("${directory.ehcache-path}")
    private String ehcachePath;

    @Bean
    public CacheManager ehCacheManager() {
        //otherCache配置
        CacheConfiguration<String, IpInfo> ipCacheConfiguration = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, IpInfo.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .heap(5, EntryUnit.ENTRIES) // 堆缓存大小为 5 个条目
                        .offheap(5, MemoryUnit.MB) // 堆外缓存大小为 10 MB
                        .disk(10, MemoryUnit.MB,true) //磁盘储存,永久储存
                )
                //.withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(10))) // 设置缓存的生存时间为 10 秒
                .withExpiry(ExpiryPolicyBuilder.noExpiration()) // 设置缓存永不过期
                .build();

        //构建
        return CacheManagerBuilder.newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(ehcachePath))//磁盘储存路径
                .withCache(IP_CACHE_NAME, ipCacheConfiguration)
                .build(true);
    }
}

