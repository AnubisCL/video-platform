package com.example.videoweb.base.listener;

import com.example.videoweb.domain.Constant;
import com.example.videoweb.domain.entity.Order;
import com.example.videoweb.domain.enums.OrderEvent;
import com.example.videoweb.domain.enums.OrderState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;

/**
 * @Author: chailei
 * @Date: 2024/10/16 16:16
 */
@Slf4j
@Component
@WithStateMachine(name = "orderStateMachine")
public class OrderStateListenerImpl {

    @OnTransition(source = "WAITING_CONFIRM", target = "WAITING_COMPLETED")
    public boolean confirmTransition(Message<OrderEvent> message) {
        log.info("状态机事件【确认创建订单】-开始");
        Order order = (Order) message.getHeaders().get(Constant.orderHeader);
        order.setOrderStatus(OrderState.WAITING_COMPLETED);
        log.info("状态机事件【确认创建订单】-结束：" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "WAITING_COMPLETED", target = "COMPLETED")
    public boolean doneTransition(Message<OrderEvent> message) {
        log.info("状态机事件【确认完成订单】-开始");
        Order order = (Order) message.getHeaders().get(Constant.orderHeader);
        order.setOrderStatus(OrderState.COMPLETED);
        log.info("状态机事件【确认完成订单】-结束：" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "WAITING_COMPLETED", target = "FAILED")
    public boolean cancelTransition(Message<OrderEvent> message){
        log.info("状态机事件【取消订单】-开始");
        Order order = (Order) message.getHeaders().get(Constant.orderHeader);
        order.setOrderStatus(OrderState.FAILED);
        log.info("状态机事件【取消订单】-结束" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "WAITING_COMPLETED", target = "WAITING_CONFIRM")
    public boolean backTransition(Message<OrderEvent> message){
        log.info("状态机事件【回退订单】-开始");
        Order order = (Order) message.getHeaders().get(Constant.orderHeader);
        order.setOrderStatus(OrderState.WAITING_CONFIRM);
        log.info("状态机事件【回退订单】-结束" + message.getHeaders().toString());
        return true;
    }

}
