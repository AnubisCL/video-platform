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
 * 
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@Getter
@Setter
@TableName("v_history")
@Schema(name = "History", description = "历史记录")
public class History {

    @Schema(description = "历史记录Id")
    @TableId(value = "history_id", type = IdType.AUTO)
    private Integer historyId;

    @Schema(description = "视频Id")
    private Long videoId;

    @Schema(description = "用户Id")
    private Long userId;

    @Schema(description = "观看次数")
    private Long count;

    @Schema(description = "0:正常,1:失效")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}
