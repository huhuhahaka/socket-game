package com.fruits.cherry.service;


import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScoketClient implements WebSocketService{

    @Autowired
    private WebSocketClient webSocketClient;

    @Override
    public void groupSending(String message) {
        // 实际这样写就行了 webSocketClient.send(message)
        webSocketClient.send(message);
    }

    @Override
    public void appointSending(String name, String message) {
        // 这里指定发送的规则由服务端决定参数格式
        webSocketClient.send("TOUSER"+name+";"+message);
    }
}