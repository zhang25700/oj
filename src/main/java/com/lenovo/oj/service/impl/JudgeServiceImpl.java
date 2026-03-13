package com.lenovo.oj.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.lenovo.oj.enums.SubmitStatusEnum;
import com.lenovo.oj.model.entity.Question;
import com.lenovo.oj.model.entity.QuestionSubmit;
import com.lenovo.oj.model.entity.User;
import com.lenovo.oj.service.CodeSandbox;
import com.lenovo.oj.service.JudgeService;
import com.lenovo.oj.service.QuestionService;
import com.lenovo.oj.service.QuestionSubmitService;
import com.lenovo.oj.service.RankingService;
import com.lenovo.oj.service.UserService;
import com.lenovo.oj.service.model.ExecuteCodeRequest;
import com.lenovo.oj.service.model.ExecuteCodeResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JudgeServiceImpl implements JudgeService {

    private final QuestionSubmitService questionSubmitService;
    private final QuestionService questionService;
    private final CodeSandbox codeSandbox;
    private final UserService userService;
    private final RankingService rankingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doJudge(Long submitId) {
        QuestionSubmit submit = questionSubmitService.getById(submitId);
        if (submit == null) {
            return;
        }
        submit.setStatus(SubmitStatusEnum.RUNNING.getValue());
        submit.setJudgeInfo("Running");
        questionSubmitService.updateById(submit);

        Question question = questionService.getById(submit.getQuestionId());
        if (question == null) {
            questionSubmitService.updateJudgeResult(submitId, SubmitStatusEnum.RUNTIME_ERROR.getValue(), "Problem not found", 0, 0);
            return;
        }

        JSONArray judgeCases = JSONUtil.parseArray(question.getJudgeCases());
        boolean accepted = true;
        String failMessage = "Accepted";
        int maxTime = 0;
        int maxMemory = 0;
        for (Object judgeCaseObj : judgeCases) {
            cn.hutool.json.JSONObject judgeCase = JSONUtil.parseObj(judgeCaseObj);
            ExecuteCodeRequest request = new ExecuteCodeRequest();
            request.setLanguage(submit.getLanguage());
            request.setCode(submit.getCode());
            request.setInput(judgeCase.getStr("input"));
            request.setTimeLimit(question.getTimeLimit());
            request.setMemoryLimit(question.getMemoryLimit());
            ExecuteCodeResponse response = codeSandbox.execute(request);
            maxTime = Math.max(maxTime, response.getTimeUsed());
            maxMemory = Math.max(maxMemory, response.getMemoryUsed());
            String expectedOutput = judgeCase.getStr("output");
            if (!response.isSuccess()) {
                accepted = false;
                failMessage = response.getMessage();
                break;
            }
            String actualOutput = response.getOutput() == null ? "" : response.getOutput().trim();
            String expected = expectedOutput == null ? "" : expectedOutput.trim();
            if (!expected.equals(actualOutput)) {
                accepted = false;
                failMessage = buildWrongAnswerMessage(judgeCase.getStr("input"), expected, actualOutput);
                break;
            }
        }

        int finalStatus = accepted ? SubmitStatusEnum.ACCEPTED.getValue() : SubmitStatusEnum.WRONG_ANSWER.getValue();
        questionSubmitService.updateJudgeResult(submitId, finalStatus, failMessage, maxTime, maxMemory);
        questionService.increaseSubmitCount(question.getId(), accepted);
        if (accepted) {
            userService.increaseSolvedCount(submit.getUserId());
            User user = userService.getById(submit.getUserId());
            if (user != null) {
                rankingService.recordAccepted(user.getId(), user.getUserName(), user.getSolvedCount());
            }
        }
    }

    private String buildWrongAnswerMessage(String input, String expected, String actual) {
        return "WA|"
                + encode(input)
                + "|"
                + encode(expected)
                + "|"
                + encode(actual);
    }

    private String encode(String value) {
        return Base64.getEncoder().encodeToString((value == null ? "" : value).getBytes(StandardCharsets.UTF_8));
    }
}
