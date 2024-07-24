package com.example.videoweb.service;

import com.example.videoweb.domain.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 菜单路由表 服务类
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
public interface IMenuService extends IService<Menu> {

    public List<Menu> getMenuByRoleId(Long roleId);

}
