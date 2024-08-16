package com.example.videoweb.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: chailei
 * @Date: 2024/8/16 10:16
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "base-directory")
public class BaseDirectoryProperties {

    private String backVideo; // 视频下载路径
    private String hlsVideo; // m3u8视频下载路径
    private String ehcachePath; // 缓存路径
}
