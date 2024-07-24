package com.example.videoweb.service.impl;

import com.example.videoweb.domain.entity.VideoTag;
import com.example.videoweb.mapper.VideoTagMapper;
import com.example.videoweb.service.IVideoTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 视频标签关联表 服务实现类
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@Service
public class VideoTagServiceImpl extends ServiceImpl<VideoTagMapper, VideoTag> implements IVideoTagService {

}
