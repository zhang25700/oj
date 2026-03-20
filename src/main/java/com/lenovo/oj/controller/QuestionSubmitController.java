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
/**
 * 提交相关接口。
 *
 * 同时承载正式提交、自定义运行、提交详情轮询和“我的提交记录”分页查询。
 */
public class QuestionSubmitController {

    private final QuestionSubmitService questionSubmitService;

    // 正式提交只负责入库和投递判题任务，最终结果由前端轮询获取。
    @PostMapping
    public BaseResponse<Long> submit(@Valid @RequestBody QuestionSubmitRequest request) {
        return ResultUtils.success(questionSubmitService.submitQuestion(request));
    }

    // 自定义运行走同一套沙箱，但不会写入提交记录，也不会进入异步判题队列。
    @PostMapping("/run")
    public BaseResponse<QuestionRunVO> run(@Valid @RequestBody QuestionRunRequest request) {
        return ResultUtils.success(questionSubmitService.runCode(request));
    }

    // 前端通过提交 id 轮询这一接口，直到状态从 Waiting / Running 变成最终状态。
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
