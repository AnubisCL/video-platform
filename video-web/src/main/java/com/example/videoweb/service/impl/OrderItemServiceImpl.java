package com.example.videoweb.service.impl;

import com.example.videoweb.domain.entity.OrderItem;
import com.example.videoweb.mapper.OrderItemMapper;
import com.example.videoweb.service.IOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单条目表 服务实现类
 * </p>
 *
 * @author anubis
 * @since 2024-10-19
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {

}
