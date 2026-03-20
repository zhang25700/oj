package com.lenovo.oj.model.vo;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.lenovo.oj.model.entity.Question;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
/**
 * 题目详情 / 列表视图对象。
 *
 * 从实体中剔除判题答案等不需要直接返回给前端的字段，
 * 同时把 judgeCases 解析成更友好的样例列表。
 */
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
        // judgeCases 在库里是 JSON 数组，前端题目页只需要其中的样例输入输出。
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
