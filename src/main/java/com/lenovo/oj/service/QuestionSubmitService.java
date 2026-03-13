package com.lenovo.oj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lenovo.oj.model.dto.submit.QuestionSubmitRequest;
import com.lenovo.oj.model.dto.submit.QuestionRunRequest;
import com.lenovo.oj.model.entity.QuestionSubmit;
import com.lenovo.oj.model.vo.QuestionRunVO;
import com.lenovo.oj.model.vo.QuestionSubmitVO;

public interface QuestionSubmitService extends IService<QuestionSubmit> {

    Long submitQuestion(QuestionSubmitRequest request);

    IPage<QuestionSubmitVO> pageMySubmissions(long current, long pageSize);

    QuestionSubmitVO getSubmissionDetail(Long submitId);

    QuestionRunVO runCode(QuestionRunRequest request);

    void updateJudgeResult(Long submitId, int status, String judgeInfo, int timeUsed, int memoryUsed);
}
