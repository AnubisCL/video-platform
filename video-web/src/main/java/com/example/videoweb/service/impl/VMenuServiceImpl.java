package com.example.videoweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.dao.VMenuDao;
import com.example.videoweb.domain.entity.VMenu;
import com.example.videoweb.domain.entity.VPermission;
import com.example.videoweb.domain.entity.VRolePermission;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.service.VMenuService;
import com.example.videoweb.service.VPermissionService;
import com.example.videoweb.service.VRolePermissionService;
import com.example.videoweb.service.VUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单路由表(VMenu)表服务实现类
 *
 * @author chailei
 * @since 2024-07-24 00:03:04
 */
@Service("vMenuService")
public class VMenuServiceImpl extends ServiceImpl<VMenuDao, VMenu> implements VMenuService {

    @Resource
    private VRolePermissionService rolePermissionService;
    @Override
    public List<VMenu> getMenuByRoleId(Long roleId) {
        Set<Long> rolePermissions = rolePermissionService.lambdaQuery()
                .select(VRolePermission::getPermissionId)
                .eq(VRolePermission::getRoleId, roleId)
                .eq(VRolePermission::getStatus, StatusEnum.YES.getStatus())
                .list().stream().map(VRolePermission::getPermissionId).collect(Collectors.toSet());
        return baseMapper.selectList(new LambdaQueryWrapper<VMenu>()
                .in(VMenu::getPermissionId, rolePermissions)
                .eq(VMenu::getStatus, StatusEnum.YES.getStatus()));
    }

}

