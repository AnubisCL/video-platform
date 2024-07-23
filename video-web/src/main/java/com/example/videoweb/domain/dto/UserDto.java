package com.example.videoweb.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * @Author: chailei
 * @Date: 2024/7/23 17:50
 */
@Data
@Builder
public class UserDto {

    private String username;
    @NotBlank(message = "密码不能为空")
    private String passwordHash;
    private String email;
    /**
     * {登录状态 @com.example.videoweb.domain.enums.SignEnum}
     */
    @NotBlank(message = "登录状态不能为空")
    private String signType;

}
