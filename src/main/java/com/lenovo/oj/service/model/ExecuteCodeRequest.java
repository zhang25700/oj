package com.lenovo.oj.service.model;

import lombok.Data;

@Data
public class ExecuteCodeRequest {

    private String language;

    private String code;

    private String input;

    private Integer timeLimit;

    private Integer memoryLimit;
}
