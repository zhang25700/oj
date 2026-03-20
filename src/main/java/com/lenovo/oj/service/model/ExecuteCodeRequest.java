package com.lenovo.oj.service.model;

import lombok.Data;

@Data
/**
 * 沙箱执行请求。
 *
 * 这是判题服务和沙箱之间的边界对象，描述一次代码运行需要的全部上下文。
 */
public class ExecuteCodeRequest {

    private String language;

    private String code;

    private String input;

    private Integer timeLimit;

    private Integer memoryLimit;
}
