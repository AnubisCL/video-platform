package com.example.videoweb.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 视频评论/弹幕表
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@Getter
@Setter
@TableName("v_review_scrolling")
@Schema(name = "ReviewScrolling", description = "视频评论/弹幕表")
public class ReviewScrolling {

    @Schema(description = "内容Id")
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Long commentId;

    @Schema(description = "上级内容Id(0:根结点)")
    private Long parentCommentId;

    @Schema(description = "视频Id")
    private Long videoId;

    @Schema(description = "用户Id")
    private Long userId;

    @Schema(description = "内容")
    private String comment;

    @Schema(description = "评论/弹幕")
    private String commentType;

    @Schema(description = "0:正常,1:失效")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}
