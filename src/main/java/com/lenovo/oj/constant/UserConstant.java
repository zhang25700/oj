package com.lenovo.oj.constant;

public final class UserConstant {

    /**
     * 前后端约定的登录 token 请求头名称。
     */
    public static final String LOGIN_TOKEN = "Authorization";
    /**
     * 登录态在 Redis 中的过期时间，当前为 7 天。
     */
    public static final long LOGIN_EXPIRE_SECONDS = 60L * 60 * 24 * 7;
    /**
     * 管理员角色标识。
     */
    public static final String ADMIN_ROLE = "admin";
    /**
     * 普通用户角色标识。
     */
    public static final String USER_ROLE = "user";

    private UserConstant() {
    }
}
