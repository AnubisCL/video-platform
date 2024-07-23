package com.example.videoweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.videoweb.domain.entity.VRole;

import java.util.Map;

/**
 * 角色表(VRole)表服务接口
 *
 * @author chailei
 * @since 2024-07-23 17:29:16
 */
public interface VRoleService extends IService<VRole> {


    public Map<String, String> getRolePermissionsByRoleId(Long roleId);
    public VRole getRoleByUserId(Long roleId);


}

