package com.lenovo.oj.model.vo;

import com.lenovo.oj.model.entity.QuestionSubmit;
import lombok.Data;

@Data
public class QuestionSubmitVO {

    private Long id;

    private Long questionId;

    private Long userId;

    private String language;

    private Integer status;

    private String judgeInfo;

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
        return vo;
    }
}
