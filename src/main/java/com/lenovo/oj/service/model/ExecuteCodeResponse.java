package com.lenovo.oj.service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**
 * 沙箱执行响应。
 *
 * 判题服务只依赖这里的统一结果，而不关心底层到底是如何编译、如何起容器的。
 */
public class ExecuteCodeResponse {

    private boolean success;

    private String output;

    private String message;

    private int timeUsed;

    private int memoryUsed;
}
