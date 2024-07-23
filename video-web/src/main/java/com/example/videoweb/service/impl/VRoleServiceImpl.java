package com.example.videoweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.dao.VRoleDao;
import com.example.videoweb.domain.entity.VPermission;
import com.example.videoweb.domain.entity.VRole;
import com.example.videoweb.domain.entity.VRolePermission;
import com.example.videoweb.domain.entity.VUser;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.service.VPermissionService;
import com.example.videoweb.service.VRolePermissionService;
import com.example.videoweb.service.VRoleService;
import com.example.videoweb.service.VUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色表(VRole)表服务实现类
 *
 * @author chailei
 * @since 2024-07-23 17:29:16
 */
@Service("vRoleService")
public class VRoleServiceImpl extends ServiceImpl<VRoleDao, VRole> implements VRoleService {

    @Resource private VRolePermissionService rolePermissionService;
    @Resource private VPermissionService permissionService;
    @Resource private VUserService userService;

    public Map<String, String> getRolePermissionsByRoleId(Long roleId) {
        Set<Long> rolePermissions = rolePermissionService.lambdaQuery()
                .select(VRolePermission::getPermissionId)
                .eq(VRolePermission::getRoleId, roleId)
                .eq(VRolePermission::getStatus, StatusEnum.YES.getStatus())
                .list().stream().map(VRolePermission::getPermissionId).collect(Collectors.toSet());
        return permissionService.lambdaQuery()
                .eq(VPermission::getStatus, StatusEnum.YES.getStatus())
                .in(VPermission::getPermissionId, rolePermissions)
                .list().stream().collect(Collectors.toMap(VPermission::getPermissionCode, VPermission::getPermissionName));
    }

    @Override
    public VRole getRoleByUserId(Long userId) {
        Long roleId = userService.lambdaQuery().eq(VUser::getUserId, userId).one().getRoleId();
        return baseMapper.selectById(roleId);
    }

}

