package com.example.videoweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.domain.Constant;
import com.example.videoweb.domain.entity.Order;
import com.example.videoweb.domain.entity.OrderItem;
import com.example.videoweb.domain.entity.Product;
import com.example.videoweb.domain.enums.OrderEvent;
import com.example.videoweb.domain.enums.OrderState;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.mapper.OrderMapper;
import com.example.videoweb.service.IOrderItemService;
import com.example.videoweb.service.IOrderService;
import com.example.videoweb.service.IProductService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: chailei
 * @Date: 2024/10/16 16:27
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Resource private StateMachine<OrderState, OrderEvent> orderStateMachine;

    @Resource private StateMachinePersister<OrderState, OrderEvent, Order> stateMachinePersister;

    @Resource private IOrderItemService orderItemService;
    @Resource private IProductService productService;

    @Override
    public Order create(Long userId) {
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalPrice(0L);
        order.setOrderStatus(OrderState.WAITING_CONFIRM);
        order.setStatus(StatusEnum.YES.getStatus());
        order.setCreateDate(new Date());
        order.setUpdateDate(new Date());
        int id = baseMapper.insert(order);
        return order;
    }

    @Override
    public Order confirm(Long orderId) {
        Order order = baseMapper.selectById(orderId);
        // 计算总价
        Long totalPrice = orderItemService.lambdaQuery()
                .eq(OrderItem::getStatus, StatusEnum.YES.getStatus())
                .eq(OrderItem::getOrderId, orderId)
                .list().stream().mapToLong(item -> {
                    Product product = productService.getById(item.getProductId());
                    return product.getPrice() * item.getQuantity();
                }).sum();
        order.setTotalPrice(totalPrice);
        Message message = MessageBuilder.withPayload(OrderEvent.CONFIRM_ORDER).setHeader(Constant.orderHeader, order).build();
        log.info("尝试确认，订单号：" + orderId);
        if (!sendEvent(message, order)) {
            log.error("尝试确认失败, 状态异常，订单号：" + orderId);
        }
        return order;
    }

    @Override
    public Order done(Long id) {
        Order order = baseMapper.selectById(id);
        Message message = MessageBuilder.withPayload(OrderEvent.DONE_ORDER).setHeader(Constant.orderHeader, order).build();
        log.info("尝试完成，订单号：" + id);
        if (!sendEvent(message, order)) {
            log.error("尝试完成失败, 状态异常，订单号：" + id);
        }
        return order;
    }

    @Override
    public Order cancel(Long id) {
        Order order = baseMapper.selectById(id);
        log.info("尝试取消，订单号：" + id);
        if (!sendEvent(MessageBuilder.withPayload(OrderEvent.CANCEL_ORDER).setHeader(Constant.orderHeader, order).build(), order)) {
            log.error("尝试取消失败，状态异常，订单号：" + id);
        }
        return order;
    }
    @Override
    public Order back(Long id) {
        Order order = baseMapper.selectById(id);
        log.info("尝试回退，订单号：" + id);
        if (!sendEvent(MessageBuilder.withPayload(OrderEvent.BACK_ORDER).setHeader(Constant.orderHeader, order).build(), order)) {
            log.error("尝试回退失败，状态异常，订单号：" + id);
        }
        return order;
    }

    @Override
    public List<Order> getOrders(Long userId) {
        return baseMapper.selectList(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, StatusEnum.YES.getStatus())
                .in(Order::getOrderStatus, OrderState.WAITING_COMPLETED, OrderState.COMPLETED, OrderState.FAILED)
                //.eq(Order::getUserId, userId) //订单只能看见自己的
                .orderByDesc(Order::getUpdateDate)
        );
    }


    /**
     * 发送订单状态转换事件
     *
     * @param message
     * @param order
     * @return
     */
    private synchronized boolean sendEvent(Message<OrderEvent> message, Order order) {
        boolean result = false;
        try {
            orderStateMachine.start();//0、开启状态机
            log.info("开启状态机，当前状态: {}", orderStateMachine.getState().getId());

            //尝试恢复状态机状态（这里没有恢复成功，导致下面状态机状态还是WAITING_CONFIRM）
            stateMachinePersister.restore(orderStateMachine, order);
            log.info("恢复状态机状态，当前状态: {}", orderStateMachine.getState().getId());

            //添加延迟用于线程安全测试
            //Thread.sleep(1000);
            result = orderStateMachine.sendEvent(message);
            log.info("发送消息后，当前状态: {}", orderStateMachine.getState().getId());
            if(!result){
                return false;
            }
            //获取到监听的结果信息
            Map<Object, Object> variables = orderStateMachine.getExtendedState().getVariables();
            //操作完成之后,删除本次对应的key信息
            //orderStateMachine.getExtendedState().getVariables().remove(CommonConstants.payTransition+order.getId());

            //持久化状态机状态
            stateMachinePersister.persist(orderStateMachine, order);
        } catch (Exception e) {
            e.printStackTrace();
            //log.error(e.getMessage());
        } finally {
            orderStateMachine.stop();
        }
        return result;
    }
}
