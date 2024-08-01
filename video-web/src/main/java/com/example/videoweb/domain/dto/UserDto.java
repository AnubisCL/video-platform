package com.example.videoweb.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: anubis
 * @Date: 2024/7/23 17:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
