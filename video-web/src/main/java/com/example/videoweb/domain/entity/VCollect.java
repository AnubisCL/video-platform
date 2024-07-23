package com.example.videoweb.domain.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Builder;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户收藏表(VCollect)表实体类
 *
 * @author chailei
 * @since 2024-07-23 17:29:15
 */
@Builder
public class VCollect extends Model<VCollect> {
    //点赞Id
    private Long collectId;
    //视频Id
    private Long videoId;
    //用户Id
    private Long userId;
    //0:正常,1:失效
    private Integer status;
    //创建时间
    private Date createDate;
    //更新时间
    private Date updateDate;


    public Long getCollectId() {
        return collectId;
    }

    public void setCollectId(Long collectId) {
        this.collectId = collectId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
        return this.collectId;
    }
}

