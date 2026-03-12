package com.lenovo.oj.model.vo;

import com.lenovo.oj.model.entity.Question;
import lombok.Data;

@Data
public class QuestionVO {

    private Long id;

    private String title;

    private String content;

    private String tags;

    private Integer timeLimit;

    private Integer memoryLimit;

    private String difficulty;

    private Integer submitCount;

    private Integer acceptedCount;

    public static QuestionVO fromEntity(Question question) {
        QuestionVO vo = new QuestionVO();
        vo.setId(question.getId());
        vo.setTitle(question.getTitle());
        vo.setContent(question.getContent());
        vo.setTags(question.getTags());
        vo.setTimeLimit(question.getTimeLimit());
        vo.setMemoryLimit(question.getMemoryLimit());
        vo.setDifficulty(question.getDifficulty());
        vo.setSubmitCount(question.getSubmitCount());
        vo.setAcceptedCount(question.getAcceptedCount());
        return vo;
    }
}
