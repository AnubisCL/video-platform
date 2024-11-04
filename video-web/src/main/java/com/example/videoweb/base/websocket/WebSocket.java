package com.example.videoweb.base.websocket;

import com.alibaba.fastjson.JSON;
import com.example.videoweb.domain.WSMessage;
import com.example.videoweb.domain.vo.ResultVo;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.EOFException;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 开启WebSocket支持
 * @Author: chailei
 * @Date: 2023-05-11 下午 4:09
 */
@Slf4j
@Component
@ServerEndpoint("/ws/{userId}")
public class WebSocket {

    // 记录当前在线连接数
    @Getter
    private static final AtomicInteger onlineCount = new AtomicInteger();

    /**
     * 存放用户信息
     */
    private static final ConcurrentHashMap<String, WebSocket> WEB_SOCKET_MAP = new ConcurrentHashMap<>(32);

    /**
     * session
     */
    private Session session;
    private String userId;

    /**
     * 实现服务器主动推送
     * @param userId
     * @param message
     * @return
     */
    public static void sendMessage(String userId, Object message) {
        WebSocket webSocket = WEB_SOCKET_MAP.get(userId);
        if (webSocket != null) {
            synchronized (webSocket.session) {
                webSocket.session.getAsyncRemote().sendText(JSON.toJSONString(ResultVo.data(message)));
            }
        }
    }

    /**
     * 有返回的发送消息
     * @param userId
     * @param message
     * @return
     */
    public static Future<Void> sendMessageFuture(String userId, Object message) {
        WebSocket webSocket = WEB_SOCKET_MAP.get(userId);
        if (webSocket != null) {
            synchronized (webSocket.session) {
                return webSocket.session.getAsyncRemote().sendText(JSON.toJSONString(ResultVo.data(message)));
            }
        }
        return null;
    }

    /**
     * 群发消息
     * @param message
     */
    public static void sendMessage(Object message) {
        WEB_SOCKET_MAP.forEach((k, v) -> {
            synchronized (v.session) {
                v.session.getAsyncRemote().sendText(JSON.toJSONString(ResultVo.data(message)));
            }
        });
    }

    /**
     * 当有新的WebSocket连接完成时
     * @param session
     * @param userId
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        //根据token获取用户信息
        this.userId = userId;
        log.info("当前链接数: [{}], 新链接: [{}]", getOnlineCount(), userId);
        addOnlineCount();
        WEB_SOCKET_MAP.put(this.userId, this);
    }

    /**
     * 当有WebSocket连接关闭时
     * @param session
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        log.info("一个链接关闭 : [{}]", userId);
        subOnlineCount();
        WEB_SOCKET_MAP.remove(this.userId);
        //处理自己的业务逻辑
        session.close();
    }

    /**
     * 当有WebSocket抛出异常时
     *
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {
        log.error("WebSocket链接异常==>{}:{}", this.userId, throwable.getMessage());
        if (Objects.nonNull(this.session) && !(throwable instanceof EOFException)) {
            log.error("UserId = {}, 通道ID = {}, 出错信息 = {}", userId, this.session.getId(), throwable);
        }
        if (Objects.nonNull(session) && session.isOpen()) {
            WEB_SOCKET_MAP.remove(this.userId);
            session.close();
        }
    }

    /**
     * 当接收到字符串消息时
     *
     * @param session
     * @param message
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException, EncodeException {
        if (message.equals("PING")) {
            session.getBasicRemote().sendText("PONG");
            log.info("心跳检查[{}]: [{}]", this.userId, message);
            return;
        }
        WSMessage wsMessage = JSON.parseObject(message, WSMessage.class);
        log.info("收到消息[{}]: [{}]", this.userId, wsMessage);
    }

    public static void addOnlineCount() {
        onlineCount.incrementAndGet();
    }

    public static void subOnlineCount() {
        onlineCount.decrementAndGet();
    }

    public static Integer getOnlineCount() {
        return onlineCount.get();
    }

}
