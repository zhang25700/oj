package com.lenovo.oj.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 题目样例对象。
 *
 * 从 judgeCases 中提取“输入 / 输出”后返回给题目详情页展示。
 */
public class QuestionSampleVO {

    private String input;

    private String output;
}
