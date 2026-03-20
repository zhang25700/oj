package com.lenovo.oj.exception;

import com.lenovo.oj.common.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    /**
     * 业务错误码，最终会被统一异常处理器转换成 BaseResponse 返回给前端。
     */
    private final int code;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
