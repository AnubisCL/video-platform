package com.example.videoweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.domain.entity.Tag;
import com.example.videoweb.domain.entity.Video;
import com.example.videoweb.domain.entity.VideoTag;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.mapper.VideoMapper;
import com.example.videoweb.service.ITagService;
import com.example.videoweb.service.IVideoService;
import com.example.videoweb.service.IVideoTagService;
import com.example.videoweb.utils.IKAnalyzerUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 视频表 服务实现类
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements IVideoService {

    @Resource private VideoMapper videoMapper;
    @Resource private IVideoTagService videoTagService;

    @Override
    public void initVideoTag() {
        List<Video> videos = videoMapper.selectList(new LambdaQueryWrapper<Video>()
                .eq(Video::getStatus, StatusEnum.YES.getStatus()));

        videos.forEach(video -> {
            String title = video.getTitle();
            Long videoId = video.getVideoId();
            try {
                List<String> tags = IKAnalyzerUtil.cut(title);
                Video video1 = new Video();
                video1.setVideoId(videoId);
                video1.setSubheading(tags.toString());
                videoMapper.updateById(video1);
                videoTagService.saveVideoTagItem(tags, videoId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
