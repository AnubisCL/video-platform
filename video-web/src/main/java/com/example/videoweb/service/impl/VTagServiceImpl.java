package com.example.videoweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.dao.VTagDao;
import com.example.videoweb.domain.entity.VTag;
import com.example.videoweb.service.VTagService;
import org.springframework.stereotype.Service;

/**
 * 视频标签表(VTag)表服务实现类
 *
 * @author chailei
 * @since 2024-07-23 17:29:16
 */
@Service("vTagService")
public class VTagServiceImpl extends ServiceImpl<VTagDao, VTag> implements VTagService {

}

