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
 * 任务表
 * </p>
 *
 * @author anubis
 * @since 2024-07-30
 */
@Getter
@Setter
@TableName("v_task")
@Schema(name = "Task", description = "任务表")
public class Task {

    @Schema(description = "任务Id")
    @TableId(value = "task_id", type = IdType.AUTO)
    private Long taskId;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "任务类型")
    private String taskType;

    @Schema(description = "0:未开始,1:正在执行,2:执行完成,3:执行失败")
    private Integer taskStatus;

    @Schema(description = "url 地址")
    private String downloadUrl;

    @Schema(description = "下载Json信息")
    private String downloadJson;

    @Schema(description = "0:正常,1:失效")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}
