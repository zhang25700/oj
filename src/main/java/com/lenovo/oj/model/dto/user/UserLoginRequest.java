package com.lenovo.oj.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequest {

    @NotBlank
    private String userAccount;

    @NotBlank
    private String userPassword;
}
