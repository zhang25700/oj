package com.lenovo.oj.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
/**
 * 用户注册请求体。
 */
public class UserRegisterRequest {

    @NotBlank
    private String userAccount;

    @NotBlank
    private String userName;

    @NotBlank
    private String userPassword;
}
