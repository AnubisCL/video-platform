package com.example.videoweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.videoweb.domain.entity.Menu;
import com.example.videoweb.domain.entity.RolePermission;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.mapper.MenuMapper;
import com.example.videoweb.service.IMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.service.IRolePermissionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单路由表 服务实现类
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Resource private IRolePermissionService rolePermissionService;
    @Resource private MenuMapper menuMapper;
    @Override
    public List<Menu> getMenuByRoleId(Long roleId) {
        Set<Long> rolePermissions = rolePermissionService.lambdaQuery()
                .select(RolePermission::getPermissionId)
                .eq(RolePermission::getRoleId, roleId)
                .eq(RolePermission::getStatus, StatusEnum.YES.getStatus())
                .list().stream().map(RolePermission::getPermissionId).collect(Collectors.toSet());
        return menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .in(Menu::getPermissionId, rolePermissions)
                .eq(Menu::getStatus, StatusEnum.YES.getStatus()));
    }
}
