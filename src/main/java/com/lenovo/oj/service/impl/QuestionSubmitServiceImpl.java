package com.lenovo.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lenovo.oj.common.ErrorCode;
import com.lenovo.oj.enums.JudgeLanguageEnum;
import com.lenovo.oj.enums.SubmitStatusEnum;
import com.lenovo.oj.exception.BusinessException;
import com.lenovo.oj.mapper.QuestionSubmitMapper;
import com.lenovo.oj.model.dto.submit.QuestionRunRequest;
import com.lenovo.oj.model.dto.submit.QuestionSubmitRequest;
import com.lenovo.oj.model.entity.Question;
import com.lenovo.oj.model.entity.QuestionSubmit;
import com.lenovo.oj.model.entity.User;
import com.lenovo.oj.model.vo.QuestionRunVO;
import com.lenovo.oj.model.vo.QuestionSubmitVO;
import com.lenovo.oj.mq.JudgeMessageProducer;
import com.lenovo.oj.service.CodeSandbox;
import com.lenovo.oj.service.QuestionService;
import com.lenovo.oj.service.QuestionSubmitService;
import com.lenovo.oj.service.UserService;
import com.lenovo.oj.service.model.ExecuteCodeRequest;
import com.lenovo.oj.service.model.ExecuteCodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit> implements QuestionSubmitService {

    private final UserService userService;
    private final QuestionService questionService;
    private final JudgeMessageProducer judgeMessageProducer;
    private final CodeSandbox codeSandbox;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitQuestion(QuestionSubmitRequest request) {
        if (!JudgeLanguageEnum.isValid(request.getLanguage())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Unsupported language");
        }
        User loginUser = userService.getLoginUser();
        Question question = questionService.getById(request.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Problem not found");
        }
        QuestionSubmit submit = new QuestionSubmit();
        submit.setQuestionId(request.getQuestionId());
        submit.setUserId(loginUser.getId());
        submit.setLanguage(request.getLanguage());
        submit.setCode(request.getCode());
        submit.setStatus(SubmitStatusEnum.WAITING.getValue());
        submit.setJudgeInfo("Waiting");
        save(submit);

        Long submitId = submit.getId();
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    judgeMessageProducer.send(submitId);
                }
            });
        } else {
            judgeMessageProducer.send(submitId);
        }
        return submitId;
    }

    @Override
    public IPage<QuestionSubmitVO> pageMySubmissions(long current, long pageSize) {
        User loginUser = userService.getLoginUser();
        Page<QuestionSubmit> page = page(
                new Page<>(current, pageSize),
                new LambdaQueryWrapper<QuestionSubmit>()
                        .eq(QuestionSubmit::getUserId, loginUser.getId())
                        .orderByDesc(QuestionSubmit::getCreateTime)
        );
        Page<QuestionSubmitVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(QuestionSubmitVO::fromEntity).toList());
        return result;
    }

    @Override
    public QuestionSubmitVO getSubmissionDetail(Long submitId) {
        User loginUser = userService.getLoginUser();
        QuestionSubmit submit = getById(submitId);
        if (submit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Submission not found");
        }
        if (!loginUser.getId().equals(submit.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return QuestionSubmitVO.fromEntity(submit);
    }

    @Override
    public QuestionRunVO runCode(QuestionRunRequest request) {
        if (!JudgeLanguageEnum.isValid(request.getLanguage())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Unsupported language");
        }
        userService.getLoginUser();
        Question question = questionService.getById(request.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Problem not found");
        }
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setLanguage(request.getLanguage());
        executeCodeRequest.setCode(request.getCode());
        executeCodeRequest.setInput(request.getInput());
        executeCodeRequest.setTimeLimit(question.getTimeLimit());
        executeCodeRequest.setMemoryLimit(question.getMemoryLimit());
        ExecuteCodeResponse response = codeSandbox.execute(executeCodeRequest);
        return QuestionRunVO.fromResponse(response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJudgeResult(Long submitId, int status, String judgeInfo, int timeUsed, int memoryUsed) {
        QuestionSubmit submit = getById(submitId);
        if (submit == null) {
            return;
        }
        submit.setStatus(status);
        submit.setJudgeInfo(judgeInfo);
        submit.setTimeUsed(timeUsed);
        submit.setMemoryUsed(memoryUsed);
        updateById(submit);
    }
}
