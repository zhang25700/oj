package com.lenovo.oj.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lenovo.oj.common.BaseResponse;
import com.lenovo.oj.common.ResultUtils;
import com.lenovo.oj.model.dto.submit.QuestionSubmitRequest;
import com.lenovo.oj.model.dto.submit.QuestionRunRequest;
import com.lenovo.oj.model.vo.QuestionRunVO;
import com.lenovo.oj.model.vo.QuestionSubmitVO;
import com.lenovo.oj.service.QuestionSubmitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/submit")
@RequiredArgsConstructor
public class QuestionSubmitController {

    private final QuestionSubmitService questionSubmitService;

    @PostMapping
    public BaseResponse<Long> submit(@Valid @RequestBody QuestionSubmitRequest request) {
        return ResultUtils.success(questionSubmitService.submitQuestion(request));
    }

    @PostMapping("/run")
    public BaseResponse<QuestionRunVO> run(@Valid @RequestBody QuestionRunRequest request) {
        return ResultUtils.success(questionSubmitService.runCode(request));
    }

    @GetMapping("/{submitId}")
    public BaseResponse<QuestionSubmitVO> getSubmissionDetail(@PathVariable Long submitId) {
        return ResultUtils.success(questionSubmitService.getSubmissionDetail(submitId));
    }

    @GetMapping("/my/page")
    public BaseResponse<IPage<QuestionSubmitVO>> pageMySubmissions(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize) {
        return ResultUtils.success(questionSubmitService.pageMySubmissions(current, pageSize));
    }
}
