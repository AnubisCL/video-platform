package com.example.videoweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.videoweb.domain.entity.Video;

/**
 * <p>
 * 视频表 服务类
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
public interface IVideoService extends IService<Video> {

    void initVideoTag();
}
