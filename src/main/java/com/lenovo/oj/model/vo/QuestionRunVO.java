package com.lenovo.oj.model.vo;

import com.lenovo.oj.service.model.ExecuteCodeResponse;
import lombok.Data;

@Data
/**
 * 自定义输入运行结果返回对象。
 */
public class QuestionRunVO {

    private boolean success;

    private String output;

    private String message;

    private int timeUsed;

    private int memoryUsed;

    public static QuestionRunVO fromResponse(ExecuteCodeResponse response) {
        QuestionRunVO vo = new QuestionRunVO();
        vo.setSuccess(response.isSuccess());
        vo.setOutput(response.getOutput());
        vo.setMessage(response.getMessage());
        vo.setTimeUsed(response.getTimeUsed());
        vo.setMemoryUsed(response.getMemoryUsed());
        return vo;
    }
}
