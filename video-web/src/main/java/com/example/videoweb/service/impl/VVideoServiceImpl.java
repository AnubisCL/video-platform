package com.example.videoweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.dao.VVideoDao;
import com.example.videoweb.domain.entity.VVideo;
import com.example.videoweb.service.VVideoService;
import org.springframework.stereotype.Service;

/**
 * 视频表(VVideo)表服务实现类
 *
 * @author chailei
 * @since 2024-07-23 17:29:17
 */
@Service("vVideoService")
public class VVideoServiceImpl extends ServiceImpl<VVideoDao, VVideo> implements VVideoService {

}

