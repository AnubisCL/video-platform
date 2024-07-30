package com.example.videoweb.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: anubis
 * @Date: 2024/7/24 00:53
 */
@Data
@Builder
public class MenuVo {

    //菜单path
    private String path;
    //菜单名称
    private String name;
    //meta.title
    private String metaTitle;
    //meta.keepAlive
    private String metaKeepAlive;
    //meta.requireAuth
    private String metaRequireAuth;
    //menuComponent
    private String component;
}
