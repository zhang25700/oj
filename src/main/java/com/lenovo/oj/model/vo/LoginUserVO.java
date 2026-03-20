package com.lenovo.oj.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
/**
 * 登录成功后返回给前端的用户信息。
 *
 * 不返回密码等敏感信息，只返回页面恢复登录态所需的最小字段。
 */
public class LoginUserVO {

    private Long id;

    private String userName;

    private String userRole;

    private String token;
}
