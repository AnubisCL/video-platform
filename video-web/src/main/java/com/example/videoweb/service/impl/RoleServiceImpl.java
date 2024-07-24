package com.example.videoweb.service.impl;

import com.example.videoweb.domain.entity.Permission;
import com.example.videoweb.domain.entity.Role;
import com.example.videoweb.domain.entity.RolePermission;
import com.example.videoweb.domain.entity.User;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.mapper.RoleMapper;
import com.example.videoweb.service.IPermissionService;
import com.example.videoweb.service.IRolePermissionService;
import com.example.videoweb.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource private IRolePermissionService rolePermissionService;
    @Resource private IPermissionService permissionService;
    @Resource private IUserService userService;
    @Resource private RoleMapper roleMapper;

    public Map<String, String> getRolePermissionsByRoleId(Long roleId) {
        Set<Long> rolePermissions = rolePermissionService.lambdaQuery()
                .select(RolePermission::getPermissionId)
                .eq(RolePermission::getRoleId, roleId)
                .eq(RolePermission::getStatus, StatusEnum.YES.getStatus())
                .list().stream().map(RolePermission::getPermissionId).collect(Collectors.toSet());
        return permissionService.lambdaQuery()
                .eq(Permission::getStatus, StatusEnum.YES.getStatus())
                .in(Permission::getPermissionId, rolePermissions)
                .list().stream().collect(Collectors.toMap(Permission::getPermissionCode, Permission::getPermissionName));
    }

    @Override
    public Role getRoleByUserId(Long userId) {
        Long roleId = userService.lambdaQuery().eq(User::getUserId, userId).one().getRoleId();
        return roleMapper.selectById(roleId);
    }
}
