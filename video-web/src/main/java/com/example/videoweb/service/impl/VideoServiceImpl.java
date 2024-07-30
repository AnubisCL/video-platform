package com.example.videoweb.service.impl;

import com.example.videoweb.mapper.VideoMapper;
import com.example.videoweb.service.IVideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
