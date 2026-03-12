package com.lenovo.oj.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegisterRequest {

    @NotBlank
    private String userAccount;

    @NotBlank
    private String userName;

    @NotBlank
    private String userPassword;
}
