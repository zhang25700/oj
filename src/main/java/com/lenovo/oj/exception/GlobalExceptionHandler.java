package com.lenovo.oj.exception;

import com.lenovo.oj.common.BaseResponse;
import com.lenovo.oj.common.ErrorCode;
import com.lenovo.oj.common.ResultUtils;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
/**
 * 全局异常处理器。
 *
 * 作用是把分散在控制层 / 服务层抛出的异常统一收敛成前端可识别的响应结构，
 * 避免出现默认的 HTML 错误页或框架级异常堆栈直接暴露给前端。
 */
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<Void> handleBusinessException(BusinessException e) {
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, IllegalArgumentException.class})
    public BaseResponse<Void> handleParamException(Exception e) {
        return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse<Void> handleException(Exception e) {
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR.getCode(), e.getMessage());
    }
}
