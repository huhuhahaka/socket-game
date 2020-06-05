package com.fruits.cherry.socket;


import com.sun.org.apache.bcel.internal.generic.RETURN;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/websocket/{name}")
public class WebSocket {


    /**
     * 用于存所有的连接服务的客户端，这个对象存储是安全的
     */
    private static ConcurrentHashMap<String, List<Session>> webSocketSet = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, String> answerMap = new ConcurrentHashMap<>();


    private static String answerStr = null;
    private static String questionStr = null;


    @OnOpen
    public void OnOpen(Session session, @PathParam(value = "name") String name) throws IOException {
        if (webSocketSet.get(name) == null) {
            List<Session> sessionList = new ArrayList<>();
            sessionList.add(session);
            webSocketSet.put(name, sessionList);
        } else {
            webSocketSet.get(name).add(session);
        }
        log.info("房间 {} ，人数为 {}。", name, webSocketSet.get(name).size());

        if (webSocketSet.get(name).size() > 1) {

            sendQuestion(name);
        }
    }


    @OnClose
    public void OnClose(Session session) {
        log.info("{} 退出房间。", session.getId());
    }

    @OnMessage
    public void OnMessage(String message, Session session) throws IOException {

        log.info("服务端 收到消息：{}", message);
//        //判断是否需要指定发送，具体规则自定义
//        if(message.indexOf("TOUSER") == 0){
//            String name = message.substring(message.indexOf("TOUSER")+6,message.indexOf(";"));
//            AppointSending(name,message.substring(message.indexOf(";")+1,message.length()));
//        }else{
//            GroupSending(message);
//        if ("ready".equals(message)) {
//            sendQuestion();
//        } else {

        String[] split = message.split("---");
        String name = split[0];
        String answer = split[1];
        Integer integer = sendResult(name, answer, session);
        if (integer.equals(0)) {
            sendQuestion(name);
        }
//        }


//        for (int i = 0; i < 10; i++) {
//            setQuestion();
//            Thread.sleep(5000);
//        }

//        }

    }

    /**
     * 群发
     *
     * @param message
     */
    public void GroupSending(String message) {
        for (String name : webSocketSet.keySet()) {
            try {
                List<Session> sessionList = webSocketSet.get(name);
                for (Session session : sessionList) {
                    session.getBasicRemote().sendText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //todo 发送随机字符串
    public void sendQuestion(String name) throws IOException {
        String questionStr = arithmeticString();

        String answerStr = getAnswerStr(questionStr);


        answerMap.put(name, answerStr);


        List<Session> sessionList = webSocketSet.get(name);
        for (Session session : sessionList) {
            session.getBasicRemote().sendText(questionStr);
        }


    }

    //todo 发送结果是否正确
    public Integer sendResult(String name ,String answer, Session session) throws IOException {

        String result;
        if (answerMap.get(name).equals(answer)) {
            result = "答案 正确";
            session.getBasicRemote().sendText(result);
            return 0;
        } else {
            result = "答案 错误";
            session.getBasicRemote().sendText(result);
            return 1;
        }

    }

    public String arithmeticString() {

        String symbolStr = "+-*/";
        int a = (int) (Math.random() * 10 + 1);
        int b = (int) (Math.random() * 10 + 1);
        String symbol = String.valueOf(symbolStr.charAt((int) (Math.random() * 4 + 0)));

        String message = a + " " + symbol + " " + b;


//        questionStr = message;

        return message;

    }

    public String getAnswerStr(String message) {
        String[] split = message.split(" ");
        Integer first = Integer.valueOf(split[0]);
        String operator = split[1];
        Integer second = Integer.valueOf(split[2]);
        String answerStr;
        if ("+".equals(operator)) {
            answerStr = String.valueOf(first + second);
        } else if ("-".equals(operator)) {
            answerStr = String.valueOf(first - second);
        } else if ("*".equals(operator)) {
            answerStr = String.valueOf(first * second);
        } else {
            answerStr = String.valueOf(first / second);
        }
        return answerStr;
    }

}