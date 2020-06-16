package com.fruits.cherry.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author ggl
 * @Description WebSocket 消息关键词枚举
 * @Date 2020/6/15 14:31
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
public enum GameKeyEnum {

    START("START", "开始游戏"),

    FULL("FULL", "房间已满"),

    ARITHMETIC_DELIMITER(" ", "四则运算分割符"),

    MESSAGE_DELIMITER(">", "消息分割符");

    private String key;

    private String description;
}
