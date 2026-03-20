package com.lenovo.oj.model.dto.submit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
/**
 * 自定义输入运行请求体。
 *
 * 与正式提交不同，这个请求不会生成 question_submit 记录，只用于即时运行。
 */
public class QuestionRunRequest {

    @NotNull
    private Long questionId;

    @NotBlank
    private String language;

    @NotBlank
    private String code;

    @NotBlank
    private String input;
}
