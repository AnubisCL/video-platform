package com.example.videoweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.dao.VLikeDao;
import com.example.videoweb.domain.entity.VLike;
import com.example.videoweb.service.VLikeService;
import org.springframework.stereotype.Service;

/**
 * 视频/评论/弹幕 点赞表(VLike)表服务实现类
 *
 * @author chailei
 * @since 2024-07-23 17:29:16
 */
@Service("vLikeService")
public class VLikeServiceImpl extends ServiceImpl<VLikeDao, VLike> implements VLikeService {

}

