package com.lenovo.oj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lenovo.oj.model.dto.submit.QuestionSubmitRequest;
import com.lenovo.oj.model.dto.submit.QuestionRunRequest;
import com.lenovo.oj.model.entity.QuestionSubmit;
import com.lenovo.oj.model.vo.QuestionRunVO;
import com.lenovo.oj.model.vo.QuestionSubmitVO;

public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 正式提交代码，返回提交记录 id。
     */
    Long submitQuestion(QuestionSubmitRequest request);

    /**
     * 查询当前用户自己的提交记录分页。
     */
    IPage<QuestionSubmitVO> pageMySubmissions(long current, long pageSize);

    /**
     * 查询单条提交详情，供前端轮询判题状态。
     */
    QuestionSubmitVO getSubmissionDetail(Long submitId);

    /**
     * 自定义输入运行，不落库、不进队列，直接返回一次执行结果。
     */
    QuestionRunVO runCode(QuestionRunRequest request);

    /**
     * 判题完成后回写最终结果。
     */
    void updateJudgeResult(Long submitId, int status, String judgeInfo, int timeUsed, int memoryUsed);
}
