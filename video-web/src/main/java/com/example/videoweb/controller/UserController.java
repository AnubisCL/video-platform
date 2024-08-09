package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.example.videoweb.base.utils.BlobUtils;
import com.example.videoweb.domain.entity.User;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

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
    public ResultVo uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = StpUtil.getLoginIdAsLong();
        try {
            Blob blob = file.getInputStream().readAllBytes() != null ?
                    new SerialBlob(file.getInputStream().readAllBytes()) : null;
            userService.updateById(User.builder().userId(userId).avatarBlob(BlobUtils.blobToBytes(blob)).build());
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        return ResultVo.ok();
    }

    @GetMapping("get-avatar")
    public ResultVo getAvatar() {
        Long userId = StpUtil.getLoginIdAsLong();
        User one = userService.lambdaQuery().eq(User::getUserId, userId)
                .eq(User::getStatus, StatusEnum.YES.getStatus()).one();
        byte[] avatarBlob = one.getAvatarBlob();
        StringBuilder builder = new StringBuilder("data:image/png;base64,");
        Blob blob = BlobUtils.bytesToBlob(avatarBlob);
        String base64Image = BlobUtils.convertBlobToBase64String(blob);
        builder.append(base64Image);
        return ResultVo.data(builder.toString());
    }

}
