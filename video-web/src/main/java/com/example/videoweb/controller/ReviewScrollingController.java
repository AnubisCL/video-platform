package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 视频评论/弹幕表 前端控制器
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@SaCheckLogin
@RestController
@RequestMapping("/reviewScrolling/")
public class ReviewScrollingController {

    //todo：不区分视频的评论和视频的弹幕，要和视频时间关联加载弹幕


}
