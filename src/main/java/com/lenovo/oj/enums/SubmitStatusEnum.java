package com.lenovo.oj.enums;

import lombok.Getter;

@Getter
/**
 * 提交记录状态枚举。
 *
 * 提交记录会在 Waiting -> Running -> 最终状态 之间流转，
 * 前端页面和轮询逻辑都依赖这里的状态码约定。
 */
public enum SubmitStatusEnum {
    WAITING(0, "等待判题"),
    RUNNING(1, "判题中"),
    ACCEPTED(2, "通过"),
    WRONG_ANSWER(3, "答案错误"),
    COMPILE_ERROR(4, "编译错误"),
    RUNTIME_ERROR(5, "运行错误"),
    TIME_LIMIT_EXCEEDED(6, "超时");

    private final int value;
    private final String text;

    SubmitStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }
}
