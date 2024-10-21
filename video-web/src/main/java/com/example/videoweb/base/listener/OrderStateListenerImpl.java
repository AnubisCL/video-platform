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

import java.util.Date;

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
        order.setUpdateDate(new Date());
        log.info("状态机事件【确认创建订单】-结束：" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "WAITING_COMPLETED", target = "COMPLETED")
    public boolean doneTransition(Message<OrderEvent> message) {
        log.info("状态机事件【确认完成订单】-开始");
        Order order = (Order) message.getHeaders().get(Constant.orderHeader);
        order.setOrderStatus(OrderState.COMPLETED);
        order.setUpdateDate(new Date());

        //todo 消息推送
        //var pushLink ="https://qmsg.zendee.cn/send/<%- theme.qMsg.qMsgKey %>";
        //    <!--var siteName = "<%- config.title %>'s Blog";-->
        //    var valineButton=document.getElementsByClassName("vsubmit vbtn")[0];
        //    // var title = siteName + "上又有新评论啦~!\n";
        //
        //    function send_valine() {
        //      //获取元素信息
        //      var pageurl = document.URL;
        //      var pagename = document.title;
        //      var pushtime = new Date();
        //      var vnick = document.getElementsByClassName("vnick vinput")[0].value;
        //      var vmail = document.getElementsByClassName("vmail vinput")[0].value;
        //      var vlink = document.getElementsByClassName("vlink vinput")[0].value;
        //      var veditor = document.getElementsByClassName("veditor vinput")[0].value;
        //      let content = "有新的评论==>" +
        //        "昵称：" + vnick +
        //        " ,文章标题：" + pagename +
        //        " ,评论内容：" + veditor +
        //        " ,评论时间：" + pushtime.toLocaleString();
        //        var myHeaders = new Headers();
        //        myHeaders.append("Content-Type", "application/x-www-form-urlencoded");
        //        var urlencoded = new URLSearchParams();
        //        urlencoded.append("msg", content);
        //        var requestOptions = {
        //            method: 'POST',
        //            headers: myHeaders,
        //            body: urlencoded,
        //            redirect: 'follow'
        //        };
        //        fetch(pushLink, requestOptions)
        //            .then(response => response.text())
        //            .then(result => console.log(result))
        //            .catch(error => console.log('error', error));
        //    }
        //
        //    //监听
        //    document.body.addEventListener('click', function(e) {
        //    if(e.target.className.indexOf('vsubmit') === -1) {
        //      return;
        //    }
        //    send_valine();


        log.info("状态机事件【确认完成订单】-结束：" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "WAITING_COMPLETED", target = "FAILED")
    public boolean cancelTransition(Message<OrderEvent> message){
        log.info("状态机事件【取消订单】-开始");
        Order order = (Order) message.getHeaders().get(Constant.orderHeader);
        order.setOrderStatus(OrderState.FAILED);
        order.setUpdateDate(new Date());
        log.info("状态机事件【取消订单】-结束" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "WAITING_COMPLETED", target = "WAITING_CONFIRM")
    public boolean backTransition(Message<OrderEvent> message){
        log.info("状态机事件【回退订单】-开始");
        Order order = (Order) message.getHeaders().get(Constant.orderHeader);
        order.setOrderStatus(OrderState.WAITING_CONFIRM);
        order.setUpdateDate(new Date());
        log.info("状态机事件【回退订单】-结束" + message.getHeaders().toString());
        return true;
    }

}
