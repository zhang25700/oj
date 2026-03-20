package com.lenovo.oj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lenovo.oj.model.dto.question.QuestionAddRequest;
import com.lenovo.oj.model.dto.question.QuestionQueryRequest;
import com.lenovo.oj.model.entity.Question;
import com.lenovo.oj.model.vo.QuestionVO;

public interface QuestionService extends IService<Question> {

    /**
     * 新增题目。
     */
    Long addQuestion(QuestionAddRequest request);

    /**
     * 分页查询题目列表。
     */
    IPage<QuestionVO> pageQuestions(QuestionQueryRequest request);

    /**
     * 获取题目详情。
     */
    QuestionVO getQuestionDetail(Long id);

    /**
     * 更新题目的提交次数和通过次数统计。
     */
    void increaseSubmitCount(Long questionId, boolean accepted);
}
