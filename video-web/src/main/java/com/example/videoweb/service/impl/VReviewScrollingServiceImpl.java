package com.example.videoweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.dao.VReviewScrollingDao;
import com.example.videoweb.domain.entity.VReviewScrolling;
import com.example.videoweb.service.VReviewScrollingService;
import org.springframework.stereotype.Service;

/**
 * 视频评论/弹幕表(VReviewScrolling)表服务实现类
 *
 * @author chailei
 * @since 2024-07-23 17:29:16
 */
@Service("vReviewScrollingService")
public class VReviewScrollingServiceImpl extends ServiceImpl<VReviewScrollingDao, VReviewScrolling> implements VReviewScrollingService {

}

