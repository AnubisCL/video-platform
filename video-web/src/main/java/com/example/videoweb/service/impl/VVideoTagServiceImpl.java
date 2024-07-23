package com.example.videoweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.dao.VVideoTagDao;
import com.example.videoweb.domain.entity.VVideoTag;
import com.example.videoweb.service.VVideoTagService;
import org.springframework.stereotype.Service;

/**
 * 视频标签关联表(VVideoTag)表服务实现类
 *
 * @author chailei
 * @since 2024-07-23 17:29:17
 */
@Service("vVideoTagService")
public class VVideoTagServiceImpl extends ServiceImpl<VVideoTagDao, VVideoTag> implements VVideoTagService {

}

