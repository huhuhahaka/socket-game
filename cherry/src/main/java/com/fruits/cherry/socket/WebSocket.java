package com.fruits.cherry.socket;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/websocket/{name}")
public class WebSocket {


    /**
     *  用于存所有的连接服务的客户端，这个对象存储是安全的
     */
    private static ConcurrentHashMap<String, List<Session>> webSocketSet = new ConcurrentHashMap<>();


    @OnOpen
    public void OnOpen(Session session, @PathParam(value = "name") String name){
        if(webSocketSet.get(name) == null){
            List<Session> sessionList = new ArrayList<>();
            sessionList.add(session);
            webSocketSet.put(name, sessionList);
        }else {
            webSocketSet.get(name).add(session);
        }
        log.info("房间 {} ，人数为 {}。",name,webSocketSet.get(name).size());
    }


    @OnClose
    public void OnClose(Session session){
        log.info("{} 退出房间。",session.getId());
    }

    @OnMessage
    public void OnMessage(String message){
//        log.info("[WebSocket] 收到消息：{}",message);
//        //判断是否需要指定发送，具体规则自定义
//        if(message.indexOf("TOUSER") == 0){
//            String name = message.substring(message.indexOf("TOUSER")+6,message.indexOf(";"));
//            AppointSending(name,message.substring(message.indexOf(";")+1,message.length()));
//        }else{
            GroupSending(message);
//        }

    }

    /**
     * 群发
     * @param message
     */
    public void GroupSending(String message){
        for (String name : webSocketSet.keySet()){
            try {
                List<Session> sessionList = webSocketSet.get(name);
                for (Session session: sessionList){
                    session.getBasicRemote().sendText(message);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}