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
 * 视频表
 * </p>
 *
 * @author anubis
 * @since 2024-07-30
 */
@Getter
@Setter
@TableName("v_video")
@Schema(name = "Video", description = "视频表")
public class Video {

    @Schema(description = "视频Id")
    @TableId(value = "video_id", type = IdType.ASSIGN_ID)
    private Long videoId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "副标题")
    private String subheading;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "视频（集）")
    private Long videoSet;

    @Schema(description = "视频（集）名称")
    private String videoSetName;

    @Schema(description = "hls url 地址")
    private String hlsUrl;

    @Schema(description = "image url 地址")
    private String imageUrl;

    @Schema(description = "0:正常,1:失效")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}
