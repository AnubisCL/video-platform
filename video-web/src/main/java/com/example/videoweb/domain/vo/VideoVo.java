package com.example.videoweb.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: chailei
 * @Date: 2024/7/30 15:49
 */
@Data
@Builder
public class VideoVo {

    private Long videoId;
    private String imageUrl;
    private String title;
}
