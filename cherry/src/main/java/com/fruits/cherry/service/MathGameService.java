package com.fruits.cherry.service;


import org.springframework.stereotype.Component;

import static com.fruits.cherry.enums.GameKeyEnum.ARITHMETIC_DELIMITER;

/**
 * @Author ggl
 * @Description 数学计算小游戏 基础功能
 * @Date 2020/6/15 14:47
 * @Version 1.0
 */
@Component
public class MathGameService {

    private static final String OPERATIONAL_SYMBOL = "+-*/";

    /**
     * @Description 生成10以内的四则运算题目
     * @param
     * @return java.lang.String
     */
    public String arithmeticString() {

        int a = (int) (Math.random() * 10 + 1);
        int b = (int) (Math.random() * 10 + 1);
        String symbol = String.valueOf(OPERATIONAL_SYMBOL.charAt((int) (Math.random() * 4 + 0)));

        return a + ARITHMETIC_DELIMITER.getKey() + symbol + ARITHMETIC_DELIMITER.getKey() + b;

    }

    public String getAnswerStr(String questionStr) {
        String[] split = questionStr.split(ARITHMETIC_DELIMITER.getKey());
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
