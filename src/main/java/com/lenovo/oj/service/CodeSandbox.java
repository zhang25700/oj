package com.lenovo.oj.service;

import com.lenovo.oj.service.model.ExecuteCodeRequest;
import com.lenovo.oj.service.model.ExecuteCodeResponse;

public interface CodeSandbox {

    ExecuteCodeResponse execute(ExecuteCodeRequest request);
}
