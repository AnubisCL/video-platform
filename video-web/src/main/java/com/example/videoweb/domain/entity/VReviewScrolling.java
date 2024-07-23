package com.example.videoweb.domain.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Builder;

import java.io.Serializable;
import java.util.Date;

/**
 * 视频评论/弹幕表(VReviewScrolling)表实体类
 *
 * @author chailei
 * @since 2024-07-23 17:29:16
 */
@Builder
public class VReviewScrolling extends Model<VReviewScrolling> {
    //内容Id
    private Long commentId;
    //上级内容Id(0:根结点)
    private Long parentCommentId;
    //视频Id
    private Long videoId;
    //用户Id
    private Long userId;
    //内容
    private String comment;
    //评论/弹幕
    private Object commentType;
    //0:正常,1:失效
    private Integer status;
    //创建时间
    private Date createDate;
    //更新时间
    private Date updateDate;


    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Object getCommentType() {
        return commentType;
    }

    public void setCommentType(Object commentType) {
        this.commentType = commentType;
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
        return this.commentId;
    }
}

