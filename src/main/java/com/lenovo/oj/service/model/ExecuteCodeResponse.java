package com.lenovo.oj.service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExecuteCodeResponse {

    private boolean success;

    private String output;

    private String message;

    private int timeUsed;

    private int memoryUsed;
}
