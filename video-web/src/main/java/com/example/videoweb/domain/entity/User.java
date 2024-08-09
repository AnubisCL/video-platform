package com.example.videoweb.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("v_user")
@Schema(name = "User", description = "用户表")
public class User {

    @Schema(description = "用户Id")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @Schema(description = "角色Id")
    private Long roleId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户密码Hash")
    private String passwordHash;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "用户头像")
    private byte[] avatarBlob;

    @Schema(description = "0:正常,1:锁定")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}
