package com.example.videoweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.dao.VHistoryDao;
import com.example.videoweb.domain.entity.VHistory;
import com.example.videoweb.service.VHistoryService;
import org.springframework.stereotype.Service;

/**
 * (VHistory)表服务实现类
 *
 * @author chailei
 * @since 2024-07-23 17:29:16
 */
@Service("vHistoryService")
public class VHistoryServiceImpl extends ServiceImpl<VHistoryDao, VHistory> implements VHistoryService {

}

