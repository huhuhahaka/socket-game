package com.fruits.cherry.config;



import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.URI;

@Slf4j
@Component
public class WebSocketConfig {

    @Bean
    public WebSocketClient webSocketClient() {
        try {
            WebSocketClient webSocketClient = new WebSocketClient(new URI("ws://localhost:8085/websocket/suyiyi"),new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    log.info("[websocket] 连接成功");
                }


                @Override
                public void onMessage(String message) {
                    log.info("[websocket] 收到消息={}",message);

                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("[websocket] 退出连接");
                }

                @Override
                public void onError(Exception ex) {
                    log.info("[websocket] 连接错误={}",ex.getMessage());
                }
            };
            webSocketClient.connect();
            return webSocketClient;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}