package com.lenovo.oj.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUserVO {

    private Long id;

    private String userName;

    private String userRole;

    private String token;
}
