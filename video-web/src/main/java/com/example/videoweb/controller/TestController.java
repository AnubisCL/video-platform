package com.example.videoweb.controller;

import com.example.videoweb.domain.entity.Order;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.IOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: chailei
 * @Date: 2024/10/16 14:39
 */
@Slf4j
@RestController
@RequestMapping("/test/")
public class TestController {

    @Resource
    private IOrderService orderService;

    @GetMapping("create")
    public ResultVo create() {
        return ResultVo.data(orderService.create());
    }

    @GetMapping("confirm/{orderId}")
    public ResultVo confirm(@PathVariable(value = "orderId")Long orderId) {
        Order confirm = orderService.confirm(orderId);
        return ResultVo.data(confirm);
    }
    @GetMapping("done/{orderId}")
    public ResultVo done(@PathVariable(value = "orderId")Long orderId) {
        return ResultVo.data(orderService.done(orderId));
    }
    @GetMapping("back/{orderId}")
    public ResultVo back(@PathVariable(value = "orderId")Long orderId) {
        return ResultVo.data(orderService.back(orderId));
    }
    @GetMapping("cancel/{orderId}")
    public ResultVo cancel(@PathVariable(value = "orderId")Long orderId) {
        return ResultVo.data(orderService.cancel(orderId));
    }

    @GetMapping("list")
    public ResultVo list() {
        return ResultVo.data(orderService.getOrders());
    }
}
