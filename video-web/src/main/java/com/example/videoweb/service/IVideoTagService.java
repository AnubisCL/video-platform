package com.example.videoweb.service;

import com.example.videoweb.domain.entity.Tag;
import com.example.videoweb.domain.entity.VideoTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 视频标签关联表 服务类
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
public interface IVideoTagService extends IService<VideoTag> {

    public void saveVideoTagItem(List<String> tag, Long videoId);

    public List<Tag> getVideoTags(Long videoId);
}
