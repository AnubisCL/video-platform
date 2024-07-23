package com.example.videoweb.domain.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Builder;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表(VUser)表实体类
 *
 * @author chailei
 * @since 2024-07-23 17:29:16
 */
@Builder
public class VUser extends Model<VUser> {
    //用户Id
    private Long userId;
    //角色Id
    private Long roleId;
    //用户名
    private String username;
    //用户密码Hash
    private String passwordHash;
    //用户邮箱
    private String email;
    //0:正常,1:锁定
    private Integer status;
    //创建时间
    private Date createDate;
    //更新时间
    private Date updateDate;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.userId;
    }
}

