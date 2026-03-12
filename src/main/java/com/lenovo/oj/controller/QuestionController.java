package com.lenovo.oj.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lenovo.oj.common.BaseResponse;
import com.lenovo.oj.common.ResultUtils;
import com.lenovo.oj.model.dto.question.QuestionAddRequest;
import com.lenovo.oj.model.dto.question.QuestionQueryRequest;
import com.lenovo.oj.model.vo.QuestionVO;
import com.lenovo.oj.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@Valid @RequestBody QuestionAddRequest request) {
        return ResultUtils.success(questionService.addQuestion(request));
    }

    @PostMapping("/page")
    public BaseResponse<IPage<QuestionVO>> pageQuestions(@RequestBody QuestionQueryRequest request) {
        return ResultUtils.success(questionService.pageQuestions(request));
    }

    @GetMapping("/{id}")
    public BaseResponse<QuestionVO> getQuestionDetail(@PathVariable Long id) {
        return ResultUtils.success(questionService.getQuestionDetail(id));
    }
}
