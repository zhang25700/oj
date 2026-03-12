package com.lenovo.oj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lenovo.oj.model.dto.question.QuestionAddRequest;
import com.lenovo.oj.model.dto.question.QuestionQueryRequest;
import com.lenovo.oj.model.entity.Question;
import com.lenovo.oj.model.vo.QuestionVO;

public interface QuestionService extends IService<Question> {

    Long addQuestion(QuestionAddRequest request);

    IPage<QuestionVO> pageQuestions(QuestionQueryRequest request);

    QuestionVO getQuestionDetail(Long id);

    void increaseSubmitCount(Long questionId, boolean accepted);
}
