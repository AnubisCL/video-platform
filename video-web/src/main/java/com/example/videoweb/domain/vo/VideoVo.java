package com.example.videoweb.domain.vo;

import com.example.videoweb.base.annotation.ReplaceIp;
import com.example.videoweb.base.annotation.ReplaceIpEntity;
import lombok.Builder;
import lombok.Data;

/**
 * @Author: chailei
 * @Date: 2024/7/30 15:49
 */
@Data
@Builder
@ReplaceIpEntity
public class VideoVo {

    private Long videoId;
    @ReplaceIp
    private String imageUrl;
    @ReplaceIp
    private String videoUrl;
    private String title;
    private String date;
}
