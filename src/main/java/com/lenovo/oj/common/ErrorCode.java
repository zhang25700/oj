package com.lenovo.oj.common;

import lombok.Getter;

@Getter
/**
 * 全局业务错误码枚举。
 *
 * 这些错误码的定位是“业务语义”，不是 HTTP 状态码的重复包装。
 * 例如登录失效、参数错误、数据不存在，都通过统一 code 交给前端做统一处理。
 */
public enum ErrorCode {
    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "数据不存在"),
    SYSTEM_ERROR(50000, "系统内部异常");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
