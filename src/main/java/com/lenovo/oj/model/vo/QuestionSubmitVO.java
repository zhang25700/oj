package com.lenovo.oj.model.vo;

import com.lenovo.oj.model.entity.QuestionSubmit;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.Data;

@Data
/**
 * 提交记录视图对象。
 *
 * 与实体相比，这里额外做了 judgeInfo 的解析，
 * 便于前端直接展示 WA 的失败输入、期望输出和实际输出。
 */
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
        // 只有 WA 的特殊编码格式才需要拆解，其它状态保持原始 judgeInfo 即可。
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
        // WA 附加信息通过 Base64 编码存储，避免分隔符和换行破坏传输格式。
        if (value == null || value.isBlank()) {
            return "";
        }
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
