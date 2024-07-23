package com.example.videoweb.domain.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Builder;

import java.io.Serializable;
import java.util.Date;

/**
 * 视频标签关联表(VVideoTag)表实体类
 *
 * @author chailei
 * @since 2024-07-23 17:29:17
 */
@Builder
public class VVideoTag extends Model<VVideoTag> {
    //视频Id
    private Long videoId;
    //标签Id
    private Long tagId;
    //0:正常,1:失效
    private Integer status;
    //创建时间
    private Date createDate;
    //更新时间
    private Date updateDate;


    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
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
        return this.videoId;
    }
}

