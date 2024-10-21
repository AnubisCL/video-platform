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
 * 文件上传表
 * </p>
 *
 * @author anubis
 * @since 2024-10-21
 */
@Getter
@Setter
@TableName("v_file_info")
@Schema(name = "FileInfo", description = "文件上传表")
public class FileInfo {

    @Schema(description = "文件ID")
    @TableId(value = "file_id", type = IdType.AUTO)
    private Long fileId;

    @Schema(description = "用户ID关联用户表")
    private Long userId;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "文件url")
    private String fileUrl;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件大小")
    private String fileSize;

    @Schema(description = "0:正常,1:锁定")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}
