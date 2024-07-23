package com.example.videoweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.videoweb.domain.entity.VMenu;

import java.util.List;

/**
 * 菜单路由表(VMenu)表服务接口
 *
 * @author chailei
 * @since 2024-07-24 00:03:04
 */
public interface VMenuService extends IService<VMenu> {


    public List<VMenu> getMenuByRoleId(Long roleId);

}

