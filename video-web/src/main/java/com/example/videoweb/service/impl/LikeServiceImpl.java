package com.example.videoweb.service.impl;

import com.example.videoweb.domain.entity.Like;
import com.example.videoweb.mapper.LikeMapper;
import com.example.videoweb.service.ILikeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 视频/评论/弹幕 点赞表 服务实现类
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements ILikeService {

}
