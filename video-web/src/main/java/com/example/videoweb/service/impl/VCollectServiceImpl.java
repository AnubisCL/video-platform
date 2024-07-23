package com.example.videoweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.dao.VCollectDao;
import com.example.videoweb.domain.entity.VCollect;
import com.example.videoweb.service.VCollectService;
import org.springframework.stereotype.Service;

/**
 * 用户收藏表(VCollect)表服务实现类
 *
 * @author chailei
 * @since 2024-07-23 17:29:15
 */
@Service("vCollectService")
public class VCollectServiceImpl extends ServiceImpl<VCollectDao, VCollect> implements VCollectService {

}

