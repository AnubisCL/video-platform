package com.example.videoweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.dao.VPermissionDao;
import com.example.videoweb.domain.entity.VPermission;
import com.example.videoweb.service.VPermissionService;
import org.springframework.stereotype.Service;

/**
 * 权限表(VPermission)表服务实现类
 *
 * @author chailei
 * @since 2024-07-23 17:29:16
 */
@Service("vPermissionService")
public class VPermissionServiceImpl extends ServiceImpl<VPermissionDao, VPermission> implements VPermissionService {

}

