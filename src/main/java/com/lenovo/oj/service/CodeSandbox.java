package com.lenovo.oj.service;

import com.lenovo.oj.service.model.ExecuteCodeRequest;
import com.lenovo.oj.service.model.ExecuteCodeResponse;

public interface CodeSandbox {

    /**
     * 执行一次代码编译和运行。
     *
     * 这个接口刻意抽象出“沙箱能力”，让判题服务只关心：
     * - 传入代码、输入、语言、时空限制
     * - 得到输出、状态、耗时、内存
     *
     * 至于底层是 Docker、远程执行服务，还是本地模拟，实现类可以自由替换。
     */
    ExecuteCodeResponse execute(ExecuteCodeRequest request);
}
