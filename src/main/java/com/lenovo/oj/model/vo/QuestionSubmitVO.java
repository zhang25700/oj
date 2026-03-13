package com.lenovo.oj.model.vo;

import com.lenovo.oj.model.entity.QuestionSubmit;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.Data;

@Data
public class QuestionSubmitVO {

    private Long id;

    private Long questionId;

    private Long userId;

    private String language;

    private Integer status;

    private String judgeInfo;

    private String failedInput;

    private String expectedOutput;

    private String actualOutput;

    private Integer timeUsed;

    private Integer memoryUsed;

    public static QuestionSubmitVO fromEntity(QuestionSubmit submit) {
        QuestionSubmitVO vo = new QuestionSubmitVO();
        vo.setId(submit.getId());
        vo.setQuestionId(submit.getQuestionId());
        vo.setUserId(submit.getUserId());
        vo.setLanguage(submit.getLanguage());
        vo.setStatus(submit.getStatus());
        vo.setJudgeInfo(submit.getJudgeInfo());
        vo.setTimeUsed(submit.getTimeUsed());
        vo.setMemoryUsed(submit.getMemoryUsed());
        parseJudgeInfo(vo, submit.getJudgeInfo());
        return vo;
    }

    private static void parseJudgeInfo(QuestionSubmitVO vo, String judgeInfo) {
        if (judgeInfo == null || !judgeInfo.startsWith("WA|")) {
            return;
        }
        String[] parts = judgeInfo.split("\\|", -1);
        if (parts.length < 4) {
            return;
        }
        vo.setJudgeInfo("答案错误");
        vo.setFailedInput(decode(parts[1]));
        vo.setExpectedOutput(decode(parts[2]));
        vo.setActualOutput(decode(parts[3]));
    }

    private static String decode(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
