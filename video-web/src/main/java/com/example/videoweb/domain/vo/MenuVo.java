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

    private String menuPath;

    private String menuIcon;

    private String menuTitle;

}
