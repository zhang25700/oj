package com.lenovo.oj.constant;

public final class UserConstant {

    public static final String LOGIN_TOKEN = "Authorization";
    public static final long LOGIN_EXPIRE_SECONDS = 60L * 60 * 24 * 7;
    public static final String ADMIN_ROLE = "admin";
    public static final String USER_ROLE = "user";

    private UserConstant() {
    }
}
