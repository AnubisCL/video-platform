package com.example.videoweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.videoweb.domain.entity.Tag;
import com.example.videoweb.domain.entity.VideoTag;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.mapper.VideoTagMapper;
import com.example.videoweb.service.ITagService;
import com.example.videoweb.service.IVideoTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Resource private ITagService tagService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveVideoTagItem(List<String> tags, Long videoId) {
        tags.forEach(tag -> {
            if (tag.length() > 1) {
                // 检查标签是否已存在
                Tag existingTag = tagService.getOne(new LambdaQueryWrapper<Tag>()
                        .eq(Tag::getTagName, tag)
                        .eq(Tag::getStatus, StatusEnum.YES.getStatus())
                );

                Long tagId;
                if (existingTag == null) {
                    // 标签不存在，创建新标签
                    Tag tagItem = new Tag();
                    tagItem.setTagName(tag);
                    boolean save = tagService.save(tagItem);
                    if (save) {
                        tagId = tagItem.getTagId();
                    } else {
                        throw new RuntimeException("Failed to save tag: " + tag);
                    }
                } else {
                    // 标签已存在，使用现有标签ID
                    tagId = existingTag.getTagId();
                }

                // 检查视频标签关联是否已存在
                boolean exists = baseMapper.exists(new LambdaQueryWrapper<VideoTag>()
                        .eq(VideoTag::getVideoId, videoId)
                        .eq(VideoTag::getTagId, tagId)
                );

                if (!exists) {
                    // 创建或更新视频标签关联
                    VideoTag videoTag = new VideoTag();
                    videoTag.setTagId(tagId);
                    videoTag.setVideoId(videoId);
                    videoTag.setCreateDate(new Date());
                    videoTag.setUpdateDate(new Date());
                    baseMapper.insert(videoTag);
                }
            }
        });
    }


    @Override
    public List<Tag> getVideoTags(Long videoId) {
        List<VideoTag> tags = baseMapper.selectList(new LambdaQueryWrapper<VideoTag>().eq(VideoTag::getVideoId, videoId));
        List<Long> tagIds = tags.stream().map(VideoTag::getTagId).collect(Collectors.toList());
        return tagService.listByIds(tagIds).stream()
                .filter(tag -> tag.getStatus().equals(StatusEnum.YES.getStatus()))
                .limit(5)
                .collect(Collectors.toList());
    }

}
