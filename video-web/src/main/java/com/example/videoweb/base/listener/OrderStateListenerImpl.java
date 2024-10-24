package com.example.videoweb.base.listener;

import com.example.videoweb.domain.Constant;
import com.example.videoweb.domain.entity.Order;
import com.example.videoweb.domain.entity.OrderItem;
import com.example.videoweb.domain.entity.Product;
import com.example.videoweb.domain.enums.OrderEvent;
import com.example.videoweb.domain.enums.OrderState;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.service.IOrderItemService;
import com.example.videoweb.service.IProductService;
import com.example.videoweb.utils.QMsgUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: chailei
 * @Date: 2024/10/16 16:16
 */
@Slf4j
@Component
@WithStateMachine(name = "orderStateMachine")
public class OrderStateListenerImpl {

    @Value("${q-msg.enable}")
    private Boolean qMsgEnable;

    @Value("${q-msg.key}")
    private String qMsgKey;
    @Resource private IOrderItemService orderItemService;
    @Resource private IProductService productService;

    @OnTransition(source = "WAITING_CONFIRM", target = "WAITING_COMPLETED")
    public boolean confirmTransition(Message<OrderEvent> message) {
        log.info("状态机事件【确认创建订单】-开始");
        Order order = (Order) message.getHeaders().get(Constant.orderHeader);
        order.setOrderStatus(OrderState.WAITING_COMPLETED);
        order.setUpdateDate(new Date());
        toMsg(order, "订单创建");
        log.info("状态机事件【确认创建订单】-结束：" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "WAITING_COMPLETED", target = "COMPLETED")
    public boolean doneTransition(Message<OrderEvent> message) {
        log.info("状态机事件【确认完成订单】-开始");
        Order order = (Order) message.getHeaders().get(Constant.orderHeader);
        order.setOrderStatus(OrderState.COMPLETED);
        order.setUpdateDate(new Date());
        toMsg(order, "订单已完成");
        log.info("状态机事件【确认完成订单】-结束：" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "WAITING_COMPLETED", target = "FAILED")
    public boolean cancelTransition(Message<OrderEvent> message){
        log.info("状态机事件【取消订单】-开始");
        Order order = (Order) message.getHeaders().get(Constant.orderHeader);
        order.setOrderStatus(OrderState.FAILED);
        order.setUpdateDate(new Date());
        toMsg(order, "订单取消");
        log.info("状态机事件【取消订单】-结束" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "WAITING_COMPLETED", target = "WAITING_CONFIRM")
    public boolean backTransition(Message<OrderEvent> message){
        log.info("状态机事件【回退订单】-开始");
        Order order = (Order) message.getHeaders().get(Constant.orderHeader);
        order.setOrderStatus(OrderState.WAITING_CONFIRM);
        order.setUpdateDate(new Date());
        toMsg(order, "订单回退");
        log.info("状态机事件【回退订单】-结束" + message.getHeaders().toString());
        return true;
    }

    private void toMsg(Order order, String msg) {
        StringBuilder builderMsg = new StringBuilder();
        List<Long> productIds = orderItemService.lambdaQuery()
                .eq(OrderItem::getOrderId, order.getOrderId())
                .eq(OrderItem::getStatus, StatusEnum.YES.getStatus())
                .list().stream().map(item -> item.getProductId()).collect(Collectors.toList());
        List<String> productTitles = productService.lambdaQuery()
                .eq(Product::getStatus, StatusEnum.YES.getStatus())
                .in(Product::getProductId, productIds)
                .list().stream().map(product -> product.getTitle())
                .collect(Collectors.toList());

        builderMsg.append("【").append(msg).append("】")
                .append("【订单号：").append(order.getOrderId()).append("】")
                .append("【").append(order.getOrderType()).append("】")
                .append("【").append(order.getOrderDate()).append("】")
                .append("【").append(productTitles.toString()).append("】")
                .append("【备注：").append(order.getOrderRemark()).append("】")
                .append("【总价：").append(order.getTotalPrice()).append("】");
        if (qMsgEnable) {
            QMsgUtil.pushQMsg(builderMsg.toString(), qMsgKey);
        }
    }
}
