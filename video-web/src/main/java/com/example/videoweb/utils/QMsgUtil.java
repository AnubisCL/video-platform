package com.example.videoweb.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: chailei
 * @Date: 2024/10/23 16:53
 */
@Slf4j
public class QMsgUtil {

    public static void pushQMsg(String msg, String key) {
        HttpRequest post = HttpUtil.createPost("https://qmsg.zendee.cn/send/" + key);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        post.addHeaders(headers);
        post.body("msg=" + msg);
        HttpResponse execute = post.execute();
        log.info(execute.body());
    }
}
