package com.example.videoweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.dao.VRolePermissionDao;
import com.example.videoweb.domain.entity.VRolePermission;
import com.example.videoweb.service.VRolePermissionService;
import org.springframework.stereotype.Service;

/**
 * 角色权限关联表(VRolePermission)表服务实现类
 *
 * @author chailei
 * @since 2024-07-23 17:29:16
 */
@Service("vRolePermissionService")
public class VRolePermissionServiceImpl extends ServiceImpl<VRolePermissionDao, VRolePermission> implements VRolePermissionService {

}

