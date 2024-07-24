package com.example.videoweb.service.impl;

import com.example.videoweb.domain.entity.Permission;
import com.example.videoweb.mapper.PermissionMapper;
import com.example.videoweb.service.IPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

}
