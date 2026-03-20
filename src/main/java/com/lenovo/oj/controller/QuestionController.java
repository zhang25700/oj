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
/**
 * 题目相关接口。
 *
 * 面向前端暴露题目新增、分页检索、详情查询等能力，
 * 本身只负责协议转换，具体业务逻辑下沉到 QuestionService。
 */
public class QuestionController {

    private final QuestionService questionService;

    // 管理端新增题目入口，题面、标签、测试点和时空限制都由这里落库。
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@Valid @RequestBody QuestionAddRequest request) {
        return ResultUtils.success(questionService.addQuestion(request));
    }

    // 首页题库分页查询入口，支持标签、难度、关键词组合检索。
    @PostMapping("/page")
    public BaseResponse<IPage<QuestionVO>> pageQuestions(@RequestBody QuestionQueryRequest request) {
        return ResultUtils.success(questionService.pageQuestions(request));
    }

    // 题目详情页入口，会优先走 Redis 热门题缓存。
    @GetMapping("/{id}")
    public BaseResponse<QuestionVO> getQuestionDetail(@PathVariable Long id) {
        return ResultUtils.success(questionService.getQuestionDetail(id));
    }
}
