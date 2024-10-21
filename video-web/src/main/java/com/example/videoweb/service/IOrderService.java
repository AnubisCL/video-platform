package com.example.videoweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.videoweb.domain.dto.ConfirmOrderDto;
import com.example.videoweb.domain.entity.Order;

import java.util.List;

/**
 * @Author: chailei
 * @Date: 2024/10/16 16:25
 */
public interface IOrderService extends IService<Order> {

    Order  create(Long id);
    Order confirm(ConfirmOrderDto confirmOrderDto);
    Order done(Long id);
    //订单发货
    Order cancel(Long id);
    //订单收货
    Order back(Long id);
    //获取所有订单信息
    List<Order> getOrders(Long userId);
}
