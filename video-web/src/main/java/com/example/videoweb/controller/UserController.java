package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.example.videoweb.base.annotation.ApiDecrypt;
import com.example.videoweb.base.websocket.WebSocket;
import com.example.videoweb.domain.WSMessage;
import com.example.videoweb.domain.dto.UserDto;
import com.example.videoweb.domain.entity.User;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.domain.vo.UserVo;
import com.example.videoweb.service.IUserService;
import com.example.videoweb.utils.BlobUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

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
    @Resource private WebSocket webSocket;

    @PostMapping("upload-avatar")
    public ResultVo uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = StpUtil.getLoginIdAsLong();
        try {
            Blob blob = file.getInputStream().readAllBytes() != null ?
                    new SerialBlob(file.getInputStream().readAllBytes()) : null;
            userService.updateById(User.builder().userId(userId).avatarBlob(BlobUtil.blobToBytes(blob)).build());
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
        Blob blob = BlobUtil.bytesToBlob(avatarBlob);
        String base64Image = BlobUtil.convertBlobToBase64String(blob);
        builder.append(base64Image);
        return ResultVo.data(builder.toString());
    }

    @ApiDecrypt
    @PostMapping("updateUserInfo")
    public ResultVo updateUserInfo(@RequestBody UserDto userDto) {
        Long userId = StpUtil.getLoginIdAsLong();
        User one = userService.lambdaQuery().eq(User::getUserId, userId).one();
        if (one.getPasswordHash().equals(userDto.getPasswordHash())) {
            userService.updateById(User.builder()
                    .userId(userId)
                    .username(userDto.getUsername())
                    .passwordHash(userDto.getPasswordHash())
                    .email(userDto.getEmail())
                    .build());
            return ResultVo.ok();
        } else {
            return ResultVo.error("密码错误");
        }
    }

    @GetMapping("getOnlineUser")
    public ResultVo getOnlineUser(){
        Long userId = StpUtil.getLoginIdAsLong();//todo 限制为管理员
        HashMap<String, WebSocket> allWebSocket = webSocket.getAllWebSocket();
        // 将 allWebSocket 转换为新的 Map，键为 userId，值为 createTime
        Map<String, String> userTimeMap = allWebSocket.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getValue().getUserId(),
                        entry -> String.valueOf(DateUtil.between(entry.getValue().getCreateTime(),
                                new Date(), DateUnit.MINUTE) + "min")
                ));
        ArrayList<UserVo> userVos = new ArrayList<>();
        userTimeMap.forEach((k, v) -> {
            User user = userService.getById(k);
            Optional.ofNullable(user).ifPresent(u -> userVos.add(
                    UserVo.builder()
                            .userId(user.getUserId())
                            .username(user.getUsername())
                            .status(u.getStatus())
                            .email(u.getEmail())
                            .onlineTime(v)
                            .build()
            ));
        });
        return ResultVo.data(userVos);
    }

    @PostMapping("pushWsMsg/{userId}")
    public ResultVo pushWsMsg(@PathVariable("userId")String userId,
                              @RequestParam("type") String type,
                              @RequestParam("msgType") String msgType,
                              @RequestParam("msg") String msg
    ) {
        WSMessage wsMessage = new WSMessage();
        wsMessage.setType(type);
        wsMessage.setMsg(msg);
        wsMessage.setMsgType(msgType);
        webSocket.sendMessage(userId, wsMessage);
        return ResultVo.ok("ok");
    }

    @PostMapping("pushWsMsgAll")
    public ResultVo pushWsMsgAll(@RequestParam("type") String type,
                                 @RequestParam("msgType") String msgType,
                                 @RequestParam("msg") String msg
    ) {
        WSMessage wsMessage = new WSMessage();
        wsMessage.setType(type);
        wsMessage.setMsg(msg);
        wsMessage.setMsgType(msgType);
        webSocket.sendMessage(wsMessage);
        return ResultVo.ok();
    }
}
