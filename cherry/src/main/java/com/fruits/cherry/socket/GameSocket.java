package com.fruits.cherry.socket;

import com.alibaba.fastjson.JSON;
import com.fruits.cherry.model.GameMessage;
import com.fruits.cherry.model.ResponseResult;
import com.fruits.cherry.service.MathGameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import static com.fruits.cherry.enums.GameKeyEnum.FULL;
import static com.fruits.cherry.enums.GameKeyEnum.MESSAGE_DELIMITER;


/**
 * @Author ggl
 * @Description 小游戏 WebSocket 服务器
 * @Date 2020/6/15 14:39
 * @Version 1.0
 */
@Slf4j
@Component
@ServerEndpoint("/gameSocket/{RoomNo}")
public class GameSocket {

    /**
     * 存储 房间与玩家的对应关系
     */
    private static ConcurrentHashMap<String, List<Session>> roomPlayerMap = new ConcurrentHashMap<>();

    /**
     * 存储 房间与答案的对应关系
     */
    private static ConcurrentHashMap<String, String> answerMap = new ConcurrentHashMap<>();
    
    @Autowired
    MathGameService mathGameService;

    
    @OnOpen
    public void OnOpen(Session session, @PathParam(value = "RoomNo") String RoomNo) throws IOException {

        if (roomPlayerMap.get(RoomNo) == null) {
            List<Session> sessionList = new ArrayList<>();
            sessionList.add(session);
            roomPlayerMap.put(RoomNo, sessionList);
        } else if (roomPlayerMap.get(RoomNo).size() < 2){
            roomPlayerMap.get(RoomNo).add(session);
        } else {
            String s = JSON.toJSONString(new GameMessage(FULL.getKey(), FULL.getDescription()));
            session.getBasicRemote().sendText(s);
        }

        log.info("房间 {} ，人数为 {}。", RoomNo, roomPlayerMap.get(RoomNo).size());

    }


    //todo 未使用新逻辑 ；对于换汤不换药的游戏模式（定义接口？） 尽量在消息中 加入游戏代号（GameCode） 动态切换处理逻辑（具体实现？）
    @OnMessage
    public void OnMessage(String message, Session session) throws IOException {

        String[] split = message.split(MESSAGE_DELIMITER.getKey());
        String RoomNo = split[0];
        String answer = split[1];
        Integer integer = sendResult(RoomNo, answer, session);
        if (integer.equals(0)) {
            sendQuestion(RoomNo);
        }

    }


    @OnClose
    public void OnClose(Session session) {
        log.info("{} 退出房间。", session.getId());
    }


    //todo sendQuestion(...)、sendResult(...) 方法中 session.getBasicRemote().sendText(str) 需统一设置 json 相应格式
    /**
     * @Description 发送问题字符串 给指定房间的所有人
     * @param RoomNo
     * @return void
     */
    public void sendQuestion(String RoomNo) throws IOException {

        String questionStr = mathGameService.arithmeticString();
        String answerStr = mathGameService.getAnswerStr(questionStr);

        answerMap.put(RoomNo, answerStr);

        List<Session> sessionList = roomPlayerMap.get(RoomNo);
        for (Session session : sessionList) {
            session.getBasicRemote().sendText(questionStr);
        }


    }


    /**
     * @Description 发送答题结果（是否正确）
     * @param RoomNo
     * @param answer
     * @param session
     * @return java.lang.Integer
     */
    public Integer sendResult(String RoomNo ,String answer, Session session) throws IOException {

        String result;
        if (answerMap.get(RoomNo).equals(answer)) {
            result = "答案 正确";
            session.getBasicRemote().sendText(result);
            return 0;
        } else {
            result = "答案 错误";
            session.getBasicRemote().sendText(result);
            return 1;
        }

    }


}
