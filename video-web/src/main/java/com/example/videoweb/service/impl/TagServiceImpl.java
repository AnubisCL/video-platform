package com.example.videoweb.service.impl;

import com.example.videoweb.domain.entity.Tag;
import com.example.videoweb.mapper.TagMapper;
import com.example.videoweb.service.ITagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 视频标签表 服务实现类
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

}
