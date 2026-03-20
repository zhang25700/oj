package com.lenovo.oj.auth;

import com.lenovo.oj.model.entity.User;

public final class LoginUserHolder {

    /**
     * 当前请求线程对应的登录用户。
     *
     * Web 容器会复用线程，所以只在单次请求链路内有效；
     * 结束时必须由拦截器主动 remove。
     */
    private static final ThreadLocal<User> USER_HOLDER = new ThreadLocal<>();

    private LoginUserHolder() {
    }

    public static void set(User user) {
        USER_HOLDER.set(user);
    }

    public static User get() {
        return USER_HOLDER.get();
    }

    public static void remove() {
        USER_HOLDER.remove();
    }
}
