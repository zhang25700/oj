package com.lenovo.oj.auth;

import com.lenovo.oj.model.entity.User;

public final class LoginUserHolder {

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
