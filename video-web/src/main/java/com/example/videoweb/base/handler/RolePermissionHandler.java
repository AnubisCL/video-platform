package com.example.videoweb.base.handler;

import cn.dev33.satoken.stp.StpInterface;
import com.example.videoweb.domain.entity.Role;
import com.example.videoweb.service.IRoleService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * sa-token 角色权限处理器
 * @Author: anubis
 * @Date: 2024/7/23 23:03
 */
@Lazy
@Component
public class RolePermissionHandler implements StpInterface {

    @Resource private IRoleService roleService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.valueOf(String.valueOf(loginId));
        Role role =  roleService.getRoleByUserId(userId);
        Map<String, String> permissionsMap = roleService.getRolePermissionsByRoleId(role.getRoleId());
        return new ArrayList<>(permissionsMap.keySet());
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.valueOf(String.valueOf(loginId));
        List<String> roleList = new ArrayList<>();
        Role role =  roleService.getRoleByUserId(userId);
        roleList.add(role.getRoleType());
        return roleList;
    }
}
