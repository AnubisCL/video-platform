package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 下单表 前端控制器
 * </p>
 *
 * @author anubis
 * @since 2024-10-16
 */
@SaCheckLogin
@RestController
@RequestMapping("/order")
public class OrderController {

}
