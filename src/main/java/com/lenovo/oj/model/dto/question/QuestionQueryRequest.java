package com.lenovo.oj.model.dto.question;

import lombok.Data;

@Data
public class QuestionQueryRequest {

    private String keyword;

    private String difficulty;

    private String tag;

    private long current = 1;

    private long pageSize = 10;
}
