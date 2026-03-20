package com.lenovo.oj.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 统一响应体。
 *
 * 前端所有接口都按同一结构解析：
 * - code: 业务状态码
 * - data: 实际业务数据
 * - message: 补充说明或错误原因
 *
 * 统一封装的好处是前端拦截器可以集中处理成功 / 失败，而不用为每个接口单独写解析逻辑。
 */
public class BaseResponse<T> {

    private int code;

    private T data;

    private String message;
}
