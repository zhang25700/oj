package com.lenovo.oj.model.vo;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.lenovo.oj.model.entity.Question;
import java.util.ArrayList;
import java.util.List;
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

    private List<QuestionSampleVO> samples;

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
        vo.setSamples(parseSamples(question.getJudgeCases()));
        return vo;
    }

    private static List<QuestionSampleVO> parseSamples(String judgeCases) {
        List<QuestionSampleVO> result = new ArrayList<>();
        if (judgeCases == null || judgeCases.isBlank()) {
            return result;
        }
        JSONArray array = JSONUtil.parseArray(judgeCases);
        for (Object item : array) {
            cn.hutool.json.JSONObject obj = JSONUtil.parseObj(item);
            result.add(new QuestionSampleVO(obj.getStr("input"), obj.getStr("output")));
        }
        return result;
    }
}
