package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.example.videoweb.domain.entity.User;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@SaCheckLogin
@RestController
@RequestMapping("/user/")
public class UserController {

    @Resource IUserService userService;

    @PostMapping("upload-avatar")
    public ResultVo uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        Long userId = StpUtil.getLoginIdAsLong();
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        userService.updateById(User.builder().userId(userId).avatarBase64(base64Image).build());
        return ResultVo.ok();
    }

    @GetMapping("get-avatar")
    public ResultVo getAvatar() {
        Long userId = StpUtil.getLoginIdAsLong();
        User one = userService.lambdaQuery().eq(User::getUserId, userId)
                .eq(User::getStatus, StatusEnum.YES.getStatus()).one();
        StringBuilder builder = new StringBuilder("data:image/png;base64,");
        return ResultVo.data(builder.append(one.getAvatarBase64()));
    }

}
