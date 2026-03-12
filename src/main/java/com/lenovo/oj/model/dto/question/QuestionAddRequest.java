package com.lenovo.oj.model.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionAddRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String tags;

    @NotBlank
    private String answer;

    @NotBlank
    private String judgeCases;

    @NotNull
    private Integer timeLimit;

    @NotNull
    private Integer memoryLimit;

    @NotBlank
    private String difficulty;
}
