package com.lenovo.oj.model.dto.submit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
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
