package com.lenovo.oj.model.dto.submit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
/**
 * 正式提交代码请求体。
 */
public class QuestionSubmitRequest {

    @NotNull
    private Long questionId;

    @NotBlank
    private String language;

    @NotBlank
    private String code;
}
