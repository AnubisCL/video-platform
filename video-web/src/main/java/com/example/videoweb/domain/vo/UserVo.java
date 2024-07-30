package com.example.videoweb.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * @Author: anubis
 * @Date: 2024/7/24 14:04
 */
@Data
@Builder
public class UserVo {

    @Schema(description = "用户Id")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "角色Id")
    private Long roleId;

    @Schema(description = "角色名")
    private String roleName;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "0:正常,1:锁定")
    private Integer status;

    @Schema(description = "权限编码")
    private Set<String> permissions;
}
