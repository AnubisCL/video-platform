package com.example.videoweb.service;

import com.example.videoweb.domain.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
public interface IRoleService extends IService<Role> {

    public Map<String, String> getRolePermissionsByRoleId(Long roleId);
    public Role getRoleByUserId(Long roleId);

}
