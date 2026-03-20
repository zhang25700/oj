package com.lenovo.oj.service;

public interface JudgeService {

    /**
     * 根据提交 id 执行正式判题流程。
     */
    void doJudge(Long submitId);
}
