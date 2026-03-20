package com.lenovo.oj.common;

public final class ResultUtils {

    private ResultUtils() {
    }

    /**
     * 返回带数据的成功响应。
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(ErrorCode.SUCCESS.getCode(), data, ErrorCode.SUCCESS.getMessage());
    }

    /**
     * 返回不带数据的成功响应。
     */
    public static BaseResponse<Void> success() {
        return new BaseResponse<>(ErrorCode.SUCCESS.getCode(), null, ErrorCode.SUCCESS.getMessage());
    }

    /**
     * 返回枚举定义的标准错误响应。
     */
    public static BaseResponse<Void> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMessage());
    }

    /**
     * 返回自定义 message 的错误响应。
     */
    public static BaseResponse<Void> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }
}
