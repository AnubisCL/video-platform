package com.example.videoweb.domain.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Builder;

import java.io.Serializable;
import java.util.Date;

/**
 * 菜单路由表(VMenu)表实体类
 *
 * @author chailei
 * @since 2024-07-24 00:03:04
 */
@Builder
public class VMenu extends Model<VMenu> {
    //菜单Id
    private Long menuId;
    //权限Id
    private Long permissionId;
    //菜单path
    private String menuPath;
    //菜单名称
    private String menuName;
    //meta.title
    private String metaTitle;
    //meta.keepAlive
    private String metaKeepAlive;
    //meta.requireAuth
    private String metaRequireAuth;
    //menuComponent
    private String menuComponent;
    //0:正常,1:失效
    private Integer status;
    //创建时间
    private Date createDate;
    //更新时间
    private Date updateDate;


    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getMenuPath() {
        return menuPath;
    }

    public void setMenuPath(String menuPath) {
        this.menuPath = menuPath;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaKeepAlive() {
        return metaKeepAlive;
    }

    public void setMetaKeepAlive(String metaKeepAlive) {
        this.metaKeepAlive = metaKeepAlive;
    }

    public String getMetaRequireAuth() {
        return metaRequireAuth;
    }

    public void setMetaRequireAuth(String metaRequireAuth) {
        this.metaRequireAuth = metaRequireAuth;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getMenuComponent() {
        return menuComponent;
    }

    public void setMenuComponent(String menuComponent) {
        this.menuComponent = menuComponent;
    }

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.menuId;
    }
}

