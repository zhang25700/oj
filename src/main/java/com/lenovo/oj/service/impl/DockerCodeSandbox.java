package com.lenovo.oj.service.impl;

import com.lenovo.oj.service.CodeSandbox;
import com.lenovo.oj.service.model.ExecuteCodeRequest;
import com.lenovo.oj.service.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

@Component
public class DockerCodeSandbox implements CodeSandbox {

    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest request) {
        /*
         * 这里保留统一沙箱入口，真实环境中可用 Docker Java SDK 创建容器，
         * 并设置 CPU / Memory / Network / Timeout 限制后编译运行代码。
         */
        return ExecuteCodeResponse.builder()
                .success(true)
                .output("mock-output")
                .message("sandbox executed")
                .timeUsed(Math.min(request.getTimeLimit(), 32))
                .memoryUsed(Math.min(request.getMemoryLimit(), 64))
                .build();
    }
}
