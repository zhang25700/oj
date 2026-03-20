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
/**
 * 判题服务实现。
 *
 * 负责读取提交记录和题目测试点，调用沙箱执行代码，并把最终结果回写数据库。
 */
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
        // 一旦消费者开始处理，先把状态切到 Running，方便前端区分“排队中”和“判题中”。
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
        // OJ 判题按测试点逐个执行：任意一个测试点失败，就提前终止并返回该失败结果。
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
        // 只有真正 AC 才更新用户解题数和排行榜，避免错误提交污染排名。
        if (accepted) {
            userService.increaseSolvedCount(submit.getUserId());
            User user = userService.getById(submit.getUserId());
            if (user != null) {
                rankingService.recordAccepted(user.getId(), user.getUserName(), user.getSolvedCount());
            }
        }
    }

    private String buildWrongAnswerMessage(String input, String expected, String actual) {
        // WA 时把失败样例打包回前端，便于自动回填到“自定义输入评测”。
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
